package com.erikmafo.btviewer.sql;

public class Value {

    private final String value;
    private final ValueType valueType;

    public Value(String value, ValueType valueType) {

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("'value' cannot be null or empty");
        }

        if (valueType == null) {
            throw new NullPointerException("'valueType' cannot be null");
        }

        this.value = value;
        this.valueType = valueType;
    }

    public String asString() {
        return value;
    }

    public double asDouble() {
        return Double.parseDouble(value);
    }

    public float asFloat() {
        return Float.parseFloat(value);
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public boolean asBool() {
        return Boolean.parseBoolean(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    public ValueType getValueType() {
        return valueType;
    }
}
