package com.erikmafo.btviewer.sql;

import com.erikmafo.btviewer.sql.functions.FunctionExpression;

public class Value {

    public static Value from(SqlToken token) {
        Value value;
        if (token.getTokenType() == SqlTokenType.INTEGER) {
            value = new Value(token.getValue(), ValueType.NUMBER);
        } else if (token.getTokenType() == SqlTokenType.QUOTED_STRING) {
            value = new Value(token.getUnquotedValue(), ValueType.STRING);
        } else if (token.getTokenType() == SqlTokenType.FUNCTION_EXPRESSION) {
            value = FunctionExpression.evaluate(token.getSubTokens());
        } else {
            throw new IllegalArgumentException(String.format("Expected a number, quoted string or a function expression but was %s", token.getValue()));
        }

        return value;
    }

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
