package com.erikmafo.btviewer.sql;
import com.erikmafo.btviewer.util.Check;

import java.util.List;

public class SqlToken {

    private final String value;
    private final SqlTokenType tokenType;
    private final int end;
    private final List<SqlToken> subTokens;
    private final String error;

    public SqlToken(String value, SqlTokenType tokenType, int end) {
        this(value, tokenType, end, null);
    }

    public SqlToken(String value, SqlTokenType tokenType, int end, List<SqlToken> subTokens) {
        this(value, tokenType, end, subTokens, null);
    }

    public SqlToken(String value, SqlTokenType tokenType, int end, List<SqlToken> subTokens, String error) {

        Check.notNullOrEmpty(value, "value");
        Check.notNull(tokenType, "tokenType");

        this.value = value;
        this.tokenType = tokenType;
        this.end = end;
        this.subTokens = subTokens;
        this.error = error;
    }

    public List<SqlToken> getSubTokens() {
        return subTokens;
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

    public int getStart() { return end - value.length(); }

    public int getEnd() { return end; }

    public String getError() {
        return error;
    }

    public void ensureValid() {
        if (SqlTokenType.INVALID == tokenType) {
            throw new IllegalArgumentException(getError());
        }
    }

    @Override
    public String toString() {
        return "SqlToken{" +
                "value='" + value + '\'' +
                '}';
    }
}
