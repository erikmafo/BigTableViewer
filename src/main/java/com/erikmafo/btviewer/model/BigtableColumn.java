package com.erikmafo.btviewer.model;

public class BigtableColumn {

    private final String family;

    private final String qualifier;


    public BigtableColumn(String family, String qualifier) {
        this.family = family;
        this.qualifier = qualifier;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getFamily() {
        return family;
    }
}
