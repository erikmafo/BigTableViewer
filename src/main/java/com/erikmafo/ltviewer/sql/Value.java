package com.erikmafo.ltviewer.sql;

import com.erikmafo.ltviewer.sql.functions.ValueFunctionExpressionParser;
import com.erikmafo.ltviewer.util.Check;
import org.jetbrains.annotations.NotNull;

public class Value {

    private final String value;
    private final ValueType valueType;

    public static Value from(@NotNull SqlToken token) {
        Value value;
        if (token.getTokenType() == SqlTokenType.INTEGER) {
            value = new Value(token.getValue(), ValueType.NUMBER);
        } else if (token.getTokenType() == SqlTokenType.QUOTED_STRING) {
            value = new Value(token.getUnquotedValue(), ValueType.STRING);
        } else if (token.getTokenType() == SqlTokenType.FUNCTION_EXPRESSION) {
            value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        } else {
            throw new IllegalArgumentException(String.format("Expected a number, quoted string or a function expression but was %s", token.getValue()));
        }

        return value;
    }

    public Value(String value, ValueType valueType) {

        Check.notNullOrEmpty(value, "value");
        Check.notNull(valueType, "valueType");

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

    public int asInt() { return Integer.parseInt(value); }

    public short asShort() { return Short.parseShort(value); }

    public boolean asBool() {
        return Boolean.parseBoolean(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    public ValueType getValueType() {
        return valueType;
    }

    public boolean isNumber() { return valueType == ValueType.NUMBER; }
}
