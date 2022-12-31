package com.erikmafo.btviewer.sql.query;

import com.erikmafo.btviewer.sql.convert.util.DateTimeFormatUtil;
import com.erikmafo.btviewer.util.Check;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/***
 * A class that represents a literal value.
 */
public record Value (String value, ValueType valueType) {

    public Value {
        Check.notNullOrEmpty(value, "value");
        Check.notNull(valueType, "valueType");
    }

    public String asString() {
        return value;
    }

    @NotNull
    @Contract(" -> new")
    public ByteString asByteStringUtf8() {
        return ByteString.copyFromUtf8(asString());
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

    public boolean isNumber() { return valueType == ValueType.NUMBER; }

    public long asTimestamp (){
        return switch (valueType()) {
            case STRING -> DateTimeFormatUtil.toMicros(asString());
            case NUMBER -> asLong();
            default -> throw new IllegalArgumentException(
                    String.format(
                            "Could not parse %s to millis. Must be integer or date time string", asString()));
        };
    }
}
