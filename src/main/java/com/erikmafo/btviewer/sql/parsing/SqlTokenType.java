package com.erikmafo.btviewer.sql.parsing;

public enum SqlTokenType {
    INVALID,
    SELECT,
    IDENTIFIER,
    ASTERISK,
    COMMA,
    QUOTED_STRING,
    NUMBER,
    BOOL,
    FROM,
    WHERE,
    OPERATOR,
    AND,
    LIMIT,
    FUNCTION_EXPRESSION,
    FUNCTION_NAME,
    OPENING_PARENTHESES,
    CLOSING_PARENTHESES,
}
