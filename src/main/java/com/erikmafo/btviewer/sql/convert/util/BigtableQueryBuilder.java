package com.erikmafo.btviewer.sql.convert.util;

import com.erikmafo.btviewer.sql.convert.rowset.BigtableRowSet;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Query;

public class BigtableQueryBuilder {

    private String tableName;

    private BigtableRowSet rowSet;

    private Filters.Filter filter;

    private int limit = -1;

    private boolean isAggregation;

    public BigtableQueryBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public BigtableQueryBuilder setRowSet(BigtableRowSet rowSet) {
        this.rowSet = rowSet;
        return this;
    }

    public BigtableQueryBuilder setFilter(Filters.Filter filter) {
        this.filter = filter;
        return this;
    }

    public BigtableQueryBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public BigtableQueryBuilder setAggregation(boolean aggregation) {
        this.isAggregation = aggregation;
        return this;
    }

    public Query build() {
        var query = Query.create(tableName);
        applyRowSet(query);
        applyFilter(query);
        applyLimit(query);

        return query;
    }

    private void applyRowSet(Query query) {
        if (rowSet == null) {
            return;
        }

        for (var range : rowSet.ranges()) {
            query.range(range);
        }

        for (var key : rowSet.keys()) {
            query.rowKey(key);
        }
    }

    private void applyFilter(Query query) {
        if (filter == null) {
            return;
        }

        query.filter(filter);
    }

    private void applyLimit(Query query) {
        if (isAggregation || limit < 0) {
            return;
        }

        query.limit(limit);
    }
}
