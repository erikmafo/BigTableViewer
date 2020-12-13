package com.erikmafo.btviewer.sql.functions;

import java.util.Arrays;

public enum Function {

    REVERSE("REVERSE"),
    CONCAT("CONCAT");

    private final String value;

    Function(String value) {
        this.value = value;
    }

    public static Function of(String value) {
        return Arrays
                .stream(values())
                .filter(function -> function.value.equals(value))
                .findFirst()
                .orElseThrow();
    }

    public boolean matchesStartOf(String sql) {

        var matchStartOf = value + "(";

        if (sql.length() < matchStartOf.length()) {
            return false;
        }

        if (!sql.substring(0, matchStartOf.length()).equalsIgnoreCase(matchStartOf)) {
            return false;
        }

        var endParenthesesIndex = sql.indexOf(')');

        if (endParenthesesIndex == -1) {
            return false;
        }

        return true;
    }

    public String value() {
        return value;
    }

    public String extractExpression(String sql) {
        if (!matchesStartOf(sql)) {
            throw new IllegalArgumentException(sql + "does not start with a function expression of type " + this);
        }

        var numberOfOpeningParentheses = 1;
        var numberOfClosingParentheses = 0;
        var index = sql.indexOf("(") + 1;
        while (index < sql.length()) {
            var ch = sql.charAt(index);
            if (ch == '(') {
                numberOfOpeningParentheses++;
            } else if (ch == ')') {
                numberOfClosingParentheses++;
            }
            if (numberOfClosingParentheses == numberOfOpeningParentheses) {
                break;
            }
            index++;
        }

        return sql.substring(0, index + 1);
    }

    public int length() {
        return value.length();
    }
}
