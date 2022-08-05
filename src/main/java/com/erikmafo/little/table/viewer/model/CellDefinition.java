package com.erikmafo.little.table.viewer.model;

import java.util.Objects;

/**
 * Specifies how the value of a cell should be interpreted by assigning a value type.
 */
public class CellDefinition {

    private String valueType;
    private String family;
    private String qualifier;

    /**
     * Creates a new instance of {@code CellDefinition}.
     *
     * @param valueType the value type of the cell.
     * @param family the name of the column family that the cell belongs to.
     * @param qualifier the name of the column qualifier that the cell belong to.
     */
    public CellDefinition(String valueType, String family, String qualifier) {
        this.valueType = valueType;
        this.family = family;
        this.qualifier = qualifier;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellDefinition that = (CellDefinition) o;
        return Objects.equals(valueType, that.valueType) &&
                Objects.equals(family, that.family) &&
                Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueType, family, qualifier);
    }

    @Override
    public String toString() {
        return "CellDefinition{" +
                "valueType='" + valueType + '\'' +
                ", family='" + family + '\'' +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }
}
