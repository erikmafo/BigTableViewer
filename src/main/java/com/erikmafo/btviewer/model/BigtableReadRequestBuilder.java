package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.Query;

public class BigtableReadRequestBuilder {

    private BigtableInstance instance;
    private Query sql;

    public BigtableReadRequestBuilder setInstance(BigtableInstance instance) {
        this.instance = instance;
        return this;
    }

    public BigtableReadRequestBuilder setSql(Query sql) {
        this.sql = sql;
        return this;
    }

    public BigtableReadRequest build() {

        return new BigtableReadRequest(instance, sql);
    }

    public Query getSql() {
        return sql;
    }
}