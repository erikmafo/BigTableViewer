package com.erikmafo.btviewer.sql;

public class SqlToken {

    private final String value;
    private final SqlTokenType tokenType;

    public SqlToken(String value, SqlTokenType tokenType) {

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("'value' cannot be null or empty");
        }

        if (tokenType == null) {
            throw new NullPointerException("'tokenType' cannot be null");
        }

        this.value = value;
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public String getUnquotedValue() {
        if (SqlTokenType.QUOTED_STRING == getTokenType()) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public int getValueAsInt() {
        return Integer.parseInt(value);
    }

    public SqlTokenType getTokenType() {
        return tokenType;
    }

}
