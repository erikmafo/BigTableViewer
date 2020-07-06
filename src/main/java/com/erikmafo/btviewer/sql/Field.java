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

    public boolean isAsterisk() {
        return name.equals("*");
    }
}
