package com.erikmafo.btviewer.sql;

public enum SqlParserStep {
    QUERY_TYPE,
    SELECT_FIELD,
    SELECT_COMMA,
    SELECT_FROM,
    SELECT_FROM_TABLE,
    WHERE,
    WHERE_FIELD,
    WHERE_OPERATOR,
    WHERE_VALUE,
    WHERE_AND,
    LIMIT_VALUE,
}
