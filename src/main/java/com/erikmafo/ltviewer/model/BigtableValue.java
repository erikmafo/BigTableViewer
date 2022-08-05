package com.erikmafo.ltviewer.model;

/**
 * Represents a value extracted from a {@link BigtableCell} that is interpreted as one of the types defined in
 * {@link ValueTypeConstants}.
 */
public class BigtableValue {

    private final Object value;
    private final String type;

    public BigtableValue(Object value, String type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public String asString() {
        return value.toString();
    }

    public String getType() {
        return type;
    }
}
