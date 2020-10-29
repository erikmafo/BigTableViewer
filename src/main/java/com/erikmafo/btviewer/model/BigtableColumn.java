package com.erikmafo.btviewer.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigtableColumn that = (BigtableColumn) o;
        return Objects.equals(family, that.family) &&
                Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, qualifier);
    }
}
