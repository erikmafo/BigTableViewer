package com.erikmafo.btviewer.sql;

public enum SqlTokenType {
    INVALID,
    SELECT,
    IDENTIFIER,
    ASTERISK,
    COMMA,
    QUOTED_STRING,
    INTEGER,
    BOOL,
    FROM,
    WHERE,
    OPERATOR,
    AND,
    LIMIT,
}
