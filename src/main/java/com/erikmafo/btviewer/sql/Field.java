package com.erikmafo.btviewer.sql;


public class Field {

    private final String name;

    public Field(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isRowKey() {
        return name.toUpperCase().equals("KEY");
    }

    public boolean isTimestamp() { return name.toUpperCase().equals("TIMESTAMP"); }

    public boolean isAsterisk() {
        return name.equals("*");
    }

    public String getFamily() {
        return getFamilyAndQualifier()[0];
    }

    public void ensureHasFamilyAndQualifier() {
        if (getFamilyAndQualifier().length != 2) {
            throw new IllegalArgumentException("Field name was not on the format {family}.{qualifier}");
        }
    }

    public boolean hasQualifier() {
        return getFamilyAndQualifier().length > 1;
    }

    public String getQualifier() {
        var parts = getFamilyAndQualifier();
        if (parts.length != 2) {
            return null;
        }
        return parts[1];
    }

    private String[] getFamilyAndQualifier() {
        return name.split("\\.");
    }
}
