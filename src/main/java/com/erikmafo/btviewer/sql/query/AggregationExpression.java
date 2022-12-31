package com.erikmafo.btviewer.sql.query;

/**
 * Represents an aggregation of a field in a sql query.
 */
public record AggregationExpression(AggregationType type, Field field) {

    public AggregationExpression {
        if (type == AggregationType.SUM || type == AggregationType.AVG) {
            field.ensureHasFamilyAndQualifier(
                    "Syntax error: " + "family and qualifier must be specified in " + type + "(...)");
        }
    }
}
