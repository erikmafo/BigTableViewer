package com.erikmafo.btviewer.model;

public class BigtableReadRequestBuilder {

    private BigtableTable bigtableTable;
    private BigtableRowRange rowRange;
    private String prefix = "";
    private int limit = 1000;
    private String sql;

    public BigtableReadRequestBuilder setTable(BigtableTable bigtableTable) {
        this.bigtableTable = bigtableTable;
        return this;
    }

    public BigtableReadRequestBuilder setRowRange(BigtableRowRange rowRange) {
        this.rowRange = rowRange;
        return this;
    }

    public BigtableReadRequestBuilder setPrefix(String prefix) {
        this.prefix = prefix != null ? prefix : "";
        return this;
    }

    public BigtableReadRequestBuilder setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public BigtableReadRequestBuilder setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public BigtableReadRequest build() {

        return new BigtableReadRequest(bigtableTable, rowRange, prefix, limit, sql);
    }

    public String getSql() {
        return sql;
    }
}