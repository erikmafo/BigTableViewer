package com.erikmafo.btviewer.model;


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
}
