package com.erikmafo.btviewer.sql.convert;

import com.erikmafo.btviewer.sql.convert.filter.AggregationFilterConverter;
import com.erikmafo.btviewer.sql.convert.filter.ChainedFilterConverter;
import com.erikmafo.btviewer.sql.convert.filter.FamilyQualifierFilterConverter;
import com.erikmafo.btviewer.sql.convert.filter.RowKeyFilterConverter;
import com.erikmafo.btviewer.sql.convert.filter.SqlQueryFilterConverter;
import com.erikmafo.btviewer.sql.convert.filter.TimestampFilterConverter;
import com.erikmafo.btviewer.sql.convert.filter.ValueFilterConverter;
import com.erikmafo.btviewer.sql.convert.rowset.SqlQueryRowSetConverter;
import com.erikmafo.btviewer.sql.convert.util.BigtableQueryBuilder;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.google.cloud.bigtable.data.v2.models.Query;
import org.jetbrains.annotations.NotNull;

/**
 * Used to convert a {@link SqlQuery} to a {@link Query} that can be applied to Bigtable.
 */
public class QueryConverter {
    private final SqlQueryRowSetConverter rowSetConverter;
    private final SqlQueryFilterConverter filterConverter;

    /**
     * Creates an instance of QueryConverter from a {@link FieldValueByteStringConverter} which
     * converts field values from a sql query to byte strings.
     *
     * @param byteStringConverter an object that converts field values to byte string.
     */
    public QueryConverter(FieldValueByteStringConverter byteStringConverter) {
        rowSetConverter = new SqlQueryRowSetConverter();
        filterConverter = new ChainedFilterConverter(
                new AggregationFilterConverter(),
                new FamilyQualifierFilterConverter(),
                new RowKeyFilterConverter(),
                new TimestampFilterConverter(),
                new ValueFilterConverter(byteStringConverter));
    }

    /**
     * Converts the sql query to a query that can be applied to Bigtable.
     *
     * @return a bigtable query object
     */
    public Query convert(@NotNull SqlQuery sqlQuery) {
        return new BigtableQueryBuilder()
                .setTableName(sqlQuery.tableName())
                .setAggregation(sqlQuery.isAggregation())
                .setLimit(sqlQuery.limit())
                .setRowSet(rowSetConverter.convert(sqlQuery))
                .setFilter(filterConverter.convert(sqlQuery))
                .build();
    }
}
