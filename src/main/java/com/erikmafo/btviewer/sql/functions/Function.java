package com.erikmafo.btviewer.sql.functions;

import java.util.Arrays;

/**
 * An enumeration of all function expressions that are allowed in a {@link com.erikmafo.btviewer.sql.SqlQuery}.
 */
public enum Function {

    REVERSE("REVERSE"),
    CONCAT("CONCAT"),
    COUNT("COUNT"),
    SUM("SUM"),
    AVG("AVG");

    private final String value;

    Function(String value) {
        this.value = value;
    }

    /**
     * Returns the Function with name equal to the specified value (ignores case).
     * @param value a string representation of a {@link Function}.
     * @return a {@link Function}.
     * @throws java.util.NoSuchElementException if there is no Function with name equal to the given value.
     */
    public static Function of(String value) {
        return Arrays
                .stream(values())
                .filter(function -> function.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Checks if a sql expression starts with a call to this function.
     *
     * @param sql a sql expression, or part of a sql expression.
     * @return true if the sql expression starts with an invocation of this function, false otherwise.
     */
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

    /**
     * The name of this function.
     * @return name of the function.
     */
    public String value() {
        return value;
    }

    /**
     * Returns the number of characters in this functions name.
     * @return - the length of the sequence of characters of the function name.
     */
    public int length() {
        return value.length();
    }
}
