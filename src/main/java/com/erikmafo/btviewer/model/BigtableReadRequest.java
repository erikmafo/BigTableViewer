package com.erikmafo.btviewer.model;


public class BigtableReadRequest {

    private final BigtableTable bigtableTable;
    private final String credentialsPath;
    private final BigtableRowRange scan;

    BigtableReadRequest(BigtableTable bigtableTable, String credentialsPath, BigtableRowRange scan) {
        this.bigtableTable = bigtableTable;
        this.credentialsPath = credentialsPath;
        this.scan = scan;
    }

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public BigtableRowRange getScan() {
        return scan;
    }

    public BigtableTable getBigtableTable() { return bigtableTable; }
}
