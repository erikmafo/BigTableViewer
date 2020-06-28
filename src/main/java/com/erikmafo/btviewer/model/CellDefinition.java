package com.erikmafo.btviewer.model;

import java.util.Objects;

public class CellDefinition {

    public CellDefinition(String valueType, String family, String qualifier) {
        this.valueType = valueType;
        this.family = family;
        this.qualifier = qualifier;
    }

    private String valueType;
    private String family;
    private String qualifier;

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
}
