package com.erikmafo.btviewer.sql;

import org.jetbrains.annotations.NotNull;

public enum ReservedWord {

    SELECT("SELECT", SqlTokenType.SELECT, true),
    ASTERISK("*", SqlTokenType.ASTERISK, false),
    COMMA(",", SqlTokenType.COMMA, false),
    FROM("FROM", SqlTokenType.FROM, true),
    WHERE("WHERE", SqlTokenType.WHERE, true),
    AND("AND", SqlTokenType.AND, true),
    EQ("=", SqlTokenType.OPERATOR, false),
    NEQ("!=", SqlTokenType.OPERATOR, false),
    GEQ(">=", SqlTokenType.OPERATOR, false),
    GT(">", SqlTokenType.OPERATOR, false),
    LEQ("<=", SqlTokenType.OPERATOR, false),
    LT("<", SqlTokenType.OPERATOR, false),
    LIKE("LIKE", SqlTokenType.OPERATOR, true),
    OPENING_PARENTHESES("(", null, false),
    CLOSING_PARENTHESES(")", null, false),
    INSERT_INTO("INSERT INTO", null, true),
    VALUES("VALUES", null, true),
    UPDATE("UPDATE", null, true),
    DELETE_FROM("DELETE FROM", null, true),
    LIMIT("LIMIT", SqlTokenType.LIMIT, true);

    private final String value;
    private final SqlTokenType tokenType;
    private final boolean requireWhitespaceAfter;

    ReservedWord(String value, SqlTokenType tokenType, boolean requireWhitespaceAfter) {
        this.value = value;
        this.tokenType = tokenType;
        this.requireWhitespaceAfter = requireWhitespaceAfter;
    }

    boolean matchesStartOf(@NotNull String sql) {

        if (sql.length() < length()) {
            return false;
        }

        if (requireWhitespaceAfter &&
                sql.length() > length() &&
                !Character.isWhitespace(sql.charAt(length()))) {
            return false;
        }

        return sql.substring(0, length()).equalsIgnoreCase(value);
    }

    int length() {
        return value.length();
    }

    String value() {
        return value;
    }

    SqlTokenType tokenType() {
        return tokenType;
    }

    boolean isSupported() {
        return tokenType != null;
    }
}
