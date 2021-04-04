package com.erikmafo.btviewer.sql;

import java.util.Objects;

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
        return name.equalsIgnoreCase("KEY");
    }

    public boolean isTimestamp() { return name.equalsIgnoreCase("TIMESTAMP"); }

    public boolean isAsterisk() {
        return name.equals("*");
    }

    public String getFamily() {
        return getFamilyAndQualifier()[0];
    }

    public void ensureHasFamilyAndQualifier(String message) {
        if (getFamilyAndQualifier().length != 2) {
            throw new IllegalArgumentException(message);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    private String[] getFamilyAndQualifier() {
        return name.split("\\.");
    }
}
