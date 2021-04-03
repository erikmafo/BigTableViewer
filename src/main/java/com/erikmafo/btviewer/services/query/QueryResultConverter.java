package com.erikmafo.btviewer.services.query;

import com.erikmafo.btviewer.model.Aggregation;
import com.erikmafo.btviewer.model.BigtableCell;
import com.erikmafo.btviewer.model.QueryResultRow;
import com.erikmafo.btviewer.model.BigtableValueConverter;
import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.SqlQuery;
import com.erikmafo.btviewer.sql.functions.AggregationExpression;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryResultConverter {

    private final BigtableValueConverter valueConverter;
    private final SqlQuery sqlQuery;

    public QueryResultConverter(@NotNull SqlQuery sqlQuery, @NotNull BigtableValueConverter valueConverter) {
        this.sqlQuery = sqlQuery;
        this.valueConverter = valueConverter;
    }

    /**
     * Converts the stream of Bigtable rows to a list of {@link QueryResultRow}.
     *
     * <p>
     * Note: The number of query result rows returned from this method might be less than
     * the number of rows returned from bigtable, depending on whether aggregations are used
     * in the query supplied to the constructor {@link #QueryResultConverter(SqlQuery, BigtableValueConverter)}.
     * </p>
     *
     * @param rowStream a row stream from bigtable.
     * @return a list of query result rows.
     */
    public List<QueryResultRow> toQueryResultRows(Stream<Row> rowStream) {
        List<QueryResultRow> resultRows;
        if (sqlQuery.isAggregation()) {
            resultRows = aggregate(rowStream);
        } else {
            resultRows = readBigtableRows(rowStream);
        }

        return resultRows;
    }

    private List<QueryResultRow> aggregate(@NotNull Stream<Row> rowStream) {
        return rowStream
                .map(this::toAggregationEntries)
                .reduce(this::combineAggregationEntries)
                .map(QueryResultRow::new)
                .stream()
                .limit(sqlQuery.getLimit())
                .collect(Collectors.toList());
    }

    public List<QueryResultRow> readBigtableRows(@NotNull Stream<Row> rowStream) {
        return rowStream.map(this::toBigtableRow).collect(Collectors.toList());
    }

    @NotNull
    @Contract("_ -> new")
    private QueryResultRow toBigtableRow(@NotNull Row row) {
        return new QueryResultRow(row.getKey().toStringUtf8(), getBigtableCells(row));
    }

    @NotNull
    @Contract(pure = true)
    private Aggregation[] combineAggregationEntries(@NotNull Aggregation[] first, @NotNull Aggregation[] second) {

        for (int i = 0; i < first.length; i++) {
            first[i].updateFrom(second[i]);
        }

        return first;
    }

    @NotNull
    private Aggregation[] toAggregationEntries(@NotNull Row row) {
        var noOfAggregations = sqlQuery.getAggregations().size();
        var entries = new Aggregation[noOfAggregations];

        for (var i=0; i < noOfAggregations; i++) {
            entries[i] = createAggregationEntry(row, sqlQuery.getAggregations().get(i));
        }

        return entries;
    }

    private List<RowCell> getCells(Row row, @NotNull Field field) {
        return field.isAsterisk()
                ? row.getCells()
                : field.hasQualifier()
                    ? row.getCells(field.getFamily(), field.getQualifier())
                    : row.getCells(field.getFamily());
    }

    @NotNull
    private Aggregation createAggregationEntry(@NotNull Row row, @NotNull AggregationExpression aggregationExpression) {
        return getCells(row, aggregationExpression.getField())
                .stream()
                .filter(c -> matches(aggregationExpression.getField(), c))
                .findFirst()
                .map(rowCell -> createAggregationEntry(aggregationExpression, rowCell))
                .orElseGet(() -> createAggregationEntry(aggregationExpression));
    }

    @Contract("_ -> new")
    @NotNull
    private Aggregation createAggregationEntry(@NotNull AggregationExpression aggregationExpression) {
        return new Aggregation(aggregationExpression.getType(), aggregationExpression.getField().getName());
    }

    @NotNull
    private Aggregation createAggregationEntry(AggregationExpression aggregationExpression, RowCell cell) {
        var entry = createAggregationEntry(aggregationExpression);
        switch (aggregationExpression.getType()) {
            case AVG:
            case SUM:
                entry.updateSum(getDouble(BigtableCell.from(cell)));
            case COUNT:
                entry.setCount(1);
                break;
        }
        return entry;
    }

    private double getDouble(BigtableCell cell) {
        if (valueConverter.isNumber(cell)) {
            return ((Number)valueConverter.convert(cell)).doubleValue();
        } else {
            return 0;
        }
    }

    private boolean matches(@NotNull Field field, @NotNull RowCell cell) {
        return field.isAsterisk() || (field.hasQualifier()
                && cell.getFamily().equalsIgnoreCase(field.getFamily())
                && cell.getQualifier().toStringUtf8().equalsIgnoreCase(field.getQualifier()));
    }

    private List<BigtableCell> getBigtableCells(@NotNull Row row) {
        return row.getCells().stream().map(this::toBigtableCell).collect(Collectors.toList());
    }

    @NotNull
    @Contract("_ -> new")
    private BigtableCell toBigtableCell(@NotNull RowCell cell) {
        return new BigtableCell(
                cell.getFamily(), cell.getQualifier().toStringUtf8(), cell.getValue(), cell.getTimestamp());
    }
}