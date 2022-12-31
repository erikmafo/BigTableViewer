package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.convert.FieldValueByteStringConverter;
import com.erikmafo.btviewer.sql.convert.util.FilterUtil;
import com.erikmafo.btviewer.sql.query.Field;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.erikmafo.btviewer.sql.query.Value;
import com.erikmafo.btviewer.sql.query.WhereClause;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class ValueFilterConverter implements SqlQueryFilterConverter {

    private final FieldValueByteStringConverter byteStringConverter;

    public ValueFilterConverter(FieldValueByteStringConverter byteStringConverter) {
        this.byteStringConverter = byteStringConverter;
    }

    public Filters.Filter convert(@NotNull SqlQuery sqlQuery) {return FilterUtil.chain(getValueFilters(sqlQuery));}

    private static boolean isFieldValueExpression(@NotNull WhereClause whereClause) {
        return !whereClause.field().isTimestamp() && !whereClause.field().isRowKey();
    }

    @NotNull
    private List<Filters.Filter> getValueFilters(@NotNull SqlQuery sqlQuery) {
        return sqlQuery
                .whereClauses()
                .stream()
                .filter(ValueFilterConverter::isFieldValueExpression)
                .map(this::getValueFilter)
                .collect(Collectors.toList());
    }

    @NotNull
    private Filters.Filter getValueFilter(@NotNull WhereClause where) {
        var trueFilter = FILTERS.pass();
        var falseFilter = FILTERS.block();
        Filters.Filter valueFilter;

        switch (where.operator()) {
            case NOT_EQUAL:
                trueFilter = FILTERS.block();
                falseFilter = FILTERS.pass();
                valueFilter = FILTERS.value().exactMatch(getValueByteString(where));
                break;
            case EQUAL:
                valueFilter = FILTERS.value().exactMatch(getValueByteString(where));
                break;
            case LESS_THAN:
                valueFilter = FILTERS.value().range().endOpen(getValueByteString(where));
                break;
            case LESS_THAN_OR_EQUAL:
                valueFilter = FILTERS.value().range().endClosed(getValueByteString(where));
                break;
            case GREATER_THAN:
                valueFilter = FILTERS.value().range().startOpen(getValueByteString(where));
                break;
            case GREATER_THAN_OR_EQUAL:
                valueFilter = FILTERS.value().range().startClosed(getValueByteString(where));
                break;
            case LIKE:
                valueFilter = FILTERS.value().regex(where.getValue().asString());
                break;
            case IN:
                valueFilter = getInterleaveFilter(where.field(), where.values());
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("operator %s is not supported for filtering values", where.operator()));
        }

        var condition = FILTERS.chain()
                .filter(FILTERS.chain()
                        .filter(FILTERS.limit().cellsPerColumn(1))
                        .filter(FILTERS.family().exactMatch(where.field().getFamily()))
                        .filter(FILTERS.qualifier().exactMatch(where.field().getQualifier()))
                        .filter(valueFilter)
                        .filter(FILTERS.limit().cellsPerRow(1)));
        return FILTERS
                .condition(condition)
                .then(trueFilter)
                .otherwise(falseFilter);
    }

    private ByteString getValueByteString(@NotNull WhereClause where) {
        return byteStringConverter.convert(where.field(), where.getValue());
    }

    @NotNull
    private Filters.InterleaveFilter getInterleaveFilter(Field field, @NotNull List<Value> values) {
        var interleaveFilter = FILTERS.interleave();
        for (var value : values) {
            interleaveFilter.filter(FILTERS.value().exactMatch(getByteString(field, value)));
        }
        return interleaveFilter;
    }
    private ByteString getByteString(Field field, Value value) {
        return byteStringConverter.convert(field, value);
    }
}
