package com.erikmafo.btviewer.sql;

public class WhereClause {

    private final Field field;
    private final Operator operator;
    private final Value value;

    public WhereClause(Field field, Operator operator, Value value) {

        if (field == null) {
            throw new NullPointerException("'field' cannot be null");
        }

        if (operator == null) {
            throw new NullPointerException("'operator' cannot be null");
        }

        if (value == null) {
            throw new NullPointerException("'value' cannot be null");
        }

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
