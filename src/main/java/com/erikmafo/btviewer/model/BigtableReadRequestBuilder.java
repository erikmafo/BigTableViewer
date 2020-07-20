package com.erikmafo.btviewer.model;

import com.google.cloud.bigtable.data.v2.models.Query;

public class BigtableReadRequestBuilder {

    private BigtableInstance instance;
    private Query query;
    private long limit;

    public BigtableReadRequestBuilder setInstance(BigtableInstance instance) {
        this.instance = instance;
        return this;
    }

    public BigtableReadRequestBuilder setQuery(Query query) {
        this.query = query;
        return this;
    }

    public BigtableReadRequestBuilder setLimit(long limit) {
        this.limit = limit;
        return this;
    }

    public BigtableReadRequest build() {
        return new BigtableReadRequest(instance, query, limit);
    }
}