package com.erikmafo.btviewer.sql.query;

import java.util.List;

/**
 * Represents a SQL query.
 */
public record SqlQuery(
        QueryType queryType,
        String tableName,
        List<Field> fields,
        List<AggregationExpression> aggregationExpressions,
        List<WhereClause> whereClauses,
        int limit) {

    public boolean isAggregation() {
        return !aggregationExpressions.isEmpty();
    }
}
