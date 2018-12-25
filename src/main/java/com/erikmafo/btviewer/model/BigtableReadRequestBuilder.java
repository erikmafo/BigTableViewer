package com.erikmafo.btviewer.model;

public class BigtableReadRequestBuilder {

    private BigtableTable bigtableTable;
    private String credentialsPath;
    private BigtableRowRange scan;

    public BigtableReadRequestBuilder setBigtableTable(BigtableTable bigtableTable) {
        this.bigtableTable = bigtableTable;
        return this;
    }

    public BigtableReadRequestBuilder setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
        return this;
    }

    public BigtableReadRequestBuilder setScan(BigtableRowRange scan) {
        this.scan = scan;
        return this;
    }

    public BigtableReadRequest build() {
        return new BigtableReadRequest(bigtableTable, credentialsPath, scan);
    }
}