package com.erikmafo.btviewer.sql.parsing;

import com.erikmafo.btviewer.sql.query.AggregationType;
import com.erikmafo.btviewer.sql.query.Field;
import com.erikmafo.btviewer.sql.query.AggregationExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class AggregationExpressionParser extends FunctionExpressionParser {

    private Field field;

    /**
     * Creates an aggregation from the given sql token.
     * @param token a sql token that expresses an aggregation.
     * @return an {@link AggregationExpression}
     * @throws IllegalArgumentException if the sql token cannot be evaluated into an AggregationExpression.
     */
    public static AggregationExpression parse(@NotNull SqlToken token) {
        return AggregationExpressionParser.parse(token.getSubTokens());
    }

    public static AggregationExpression parse(List<SqlToken> tokens) {
        var expression = new AggregationExpressionParser();
        expression.read(tokens);
        return expression.parse();
    }

    protected AggregationExpression parse() {
        switch (getFunction()) {
            case COUNT: return new AggregationExpression(AggregationType.COUNT, field);
            case SUM: return new AggregationExpression(AggregationType.SUM, field);
            case AVG: return new AggregationExpression(AggregationType.AVG, field);
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
