package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.SqlToken;

/**
 * Represents an aggregation of a field in a sql query.
 */
public class Aggregation {

    /**
     * An enumeration of the different types of supported aggregations.
     */
    public enum Type {
        COUNT,
        SUM,
        AVG
    }

    /**
     * Creates an aggregation from the given sql token.
     * @param token a sql token that expresses an aggregation.
     * @return an {@link Aggregation}
     * @throws IllegalArgumentException if the sql token cannot be evaluated into an Aggregation.
     */
    public static Aggregation from(SqlToken token) {
        return AggregationExpression.evaluate(token.getSubTokens());
    }

    private final Type type;
    private final Field field;

    public Aggregation(Type type, Field field) {

        switch (type) {
            case SUM:
            case AVG:
                field.ensureHasFamilyAndQualifier(
                        "Syntax error: " + "family and qualifier must be specified in " + type + "(...)");
                break;
        }

        this.type = type;
        this.field = field;
    }

    public Type getType() {
        return type;
    }

    public Field getField() {
        return field;
    }
}
