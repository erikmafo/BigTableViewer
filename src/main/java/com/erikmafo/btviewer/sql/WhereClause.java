package com.erikmafo.btviewer.sql;

import com.erikmafo.btviewer.util.Check;

public class WhereClause {

    private final Field field;
    private final Operator operator;
    private final Value value;

    public WhereClause(Field field, Operator operator, Value value) {

        Check.notNull(field, "field");
        Check.notNull(operator, "operator");
        Check.notNull(value, "value");

        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    public Value getValue() {
        return value;
    }
}
