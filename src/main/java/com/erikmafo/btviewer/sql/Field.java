package com.erikmafo.btviewer.sql;


public class Field {

    private final String name;

    public Field(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' cannot be null or empty");
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
        return getParts()[0];
    }

    public boolean hasQualifier() {
        return getParts().length > 1;
    }

    public String getQualifier() {
        var parts = getParts();
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }

    private String[] getParts() {
        return name.split("\\.");
    }
}
