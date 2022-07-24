package com.erikmafo.btviewer.model;

import java.util.Objects;

/**
 * Represents a column (within a column family) in a bigtable table.
 */
public class BigtableColumn {

    private final String family;

    private final String qualifier;


    public BigtableColumn(String family, String qualifier) {
        this.family = family;
        this.qualifier = qualifier;
    }

    /**
     * Returns the name of the column qualifier.
     *
     * @return the name of the column qualifier.
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Returns the name of the column family that the column belongs to.
     *
     * @return the name of the column family.
     */
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

    @Override
    public String toString() {
        return "BigtableColumn{" +
                "family='" + family + '\'' +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}
