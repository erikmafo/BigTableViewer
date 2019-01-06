package com.erikmafo.btviewer.model;

import java.nio.file.Path;

public class BigtableReadRequestBuilder {

    private BigtableTable bigtableTable;
    private Path credentialsPath;
    private BigtableRowRange scan;

    public BigtableReadRequestBuilder setTable(BigtableTable bigtableTable) {
        this.bigtableTable = bigtableTable;
        return this;
    }

    public BigtableReadRequestBuilder setCredentialsPath(Path credentialsPath) {
        this.credentialsPath = credentialsPath;
        return this;
    }

    public BigtableReadRequestBuilder setRowRange(BigtableRowRange scan) {
        this.scan = scan;
        return this;
    }

    public BigtableReadRequest build() {
        return new BigtableReadRequest(bigtableTable, credentialsPath, scan);
    }
}