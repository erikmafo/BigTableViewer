package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.SqlToken;

/**
 * Represents an aggregation of a field in a sql query.
 */
public class AggregationExpression {

    private final Type type;
    private final Field field;

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
     * @return an {@link AggregationExpression}
     * @throws IllegalArgumentException if the sql token cannot be evaluated into an AggregationExpression.
     */
    public static AggregationExpression from(SqlToken token) {
        return AggregationExpressionParser.parse(token.getSubTokens());
    }

    public AggregationExpression(Type type, Field field) {

        if (type == Type.SUM || type == Type.AVG) {
            field.ensureHasFamilyAndQualifier(
                    "Syntax error: " + "family and qualifier must be specified in " + type + "(...)");
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
