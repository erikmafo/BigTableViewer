package com.erikmafo.btviewer.sql.query;

import com.erikmafo.btviewer.util.Check;

import java.util.List;

/**
 * @param field should be generalized to something like FieldExpression
 */
public record WhereClause(Field field, Operator operator, List<Value> values) {

    public WhereClause {
        Check.notNull(field, "field");
        Check.notNull(operator, "operator");
        Check.notNullOrEmpty(values, "values");
    }

    public Value getValue() {
        return values.get(0);
    }
}
