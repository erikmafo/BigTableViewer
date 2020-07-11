package com.erikmafo.btviewer.model;

import com.google.cloud.bigtable.data.v2.models.Query;

public class BigtableReadRequest {

    private final BigtableInstance instance;
    private final Query query;
    private final long limit;

    BigtableReadRequest(BigtableInstance instance, Query query, long limit) {
        this.instance = instance;
        this.query = query;
        this.limit = limit;
    }

    public BigtableInstance getInstance() {
        return instance;
    }

    public Query getQuery() {
        return query;
    }

    public long getLimit() {
        return limit;
    }
}
