package com.erikmafo.btviewer.sql;

import java.util.Arrays;

public enum Operator {
    EQUAL("="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LIKE("LIKE");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    static Operator of(String value) {
        return Arrays
                .stream(values())
                .filter(operator -> operator.value.equals(value))
                .findFirst()
                .orElseThrow();
    }
}
