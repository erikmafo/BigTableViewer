package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.SqlToken;

import java.util.List;

public class AggregationExpressionParser extends FunctionExpressionParser {

    private Field field;

    public static AggregationExpression parse(List<SqlToken> tokens) {
        var expression = new AggregationExpressionParser();
        expression.read(tokens);
        return expression.parse();
    }

    protected AggregationExpression parse() {
        switch (getFunction()) {
            case COUNT: return new AggregationExpression(AggregationExpression.Type.COUNT, field);
            case SUM: return new AggregationExpression(AggregationExpression.Type.SUM, field);
            case AVG: return new AggregationExpression(AggregationExpression.Type.AVG, field);
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
