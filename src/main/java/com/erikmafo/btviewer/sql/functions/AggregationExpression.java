package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.SqlToken;

import java.util.List;

public class AggregationExpression extends FunctionExpression {

    public static Aggregation evaluate(List<SqlToken> tokens) {
        var expression = new AggregationExpression();
        expression.read(tokens);
        return expression.evaluate();
    }

    private Field field;

    protected Aggregation evaluate() {
        switch (getFunction()) {
            case COUNT: return new Aggregation(Aggregation.Type.COUNT, field);
            case SUM: return new Aggregation(Aggregation.Type.SUM, field);
            case AVG: return new Aggregation(Aggregation.Type.AVG, field);
            default: throw new IllegalArgumentException(
                    String.format("The %s(...) expression could not be evaluated", getFunction().value()));
        }
    }

    @Override
    protected void onInputToken(SqlToken inputToken) {
        if (field != null) {
            throw new IllegalArgumentException(String.format(
                    "Invalid token %s, %s only accepts a single argument", getFunction(), inputToken.getValue()));
        }
        field = new Field(inputToken.getValue());
    }
}
