package com.erikmafo.btviewer.model;


import java.nio.file.Path;

public class BigtableReadRequest {

    private final BigtableTable bigtableTable;
    private final Path credentialsPath;
    private final BigtableRowRange scan;

    BigtableReadRequest(BigtableTable bigtableTable, Path credentialsPath, BigtableRowRange scan) {
        this.bigtableTable = bigtableTable;
        this.credentialsPath = credentialsPath;
        this.scan = scan;
    }

    public Path getCredentialsPath() {
        return credentialsPath;
    }

    public BigtableRowRange getScan() {
        return scan;
    }

    public BigtableTable getBigtableTable() { return bigtableTable; }
}
