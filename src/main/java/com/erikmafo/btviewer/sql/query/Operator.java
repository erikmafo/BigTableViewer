package com.erikmafo.btviewer.sql.query;

import java.util.Arrays;

public enum Operator {
    EQUAL("="),
    NOT_EQUAL("!="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LIKE("LIKE"),
    IN("IN");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    public static Operator of(String value) {
        return Arrays
                .stream(values())
                .filter(operator -> operator.value.equals(value))
                .findFirst()
                .orElseThrow();
    }
}
