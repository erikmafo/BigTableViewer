package com.erikmafo.btviewer.sql.parsing;

import com.erikmafo.btviewer.sql.query.Value;
import com.erikmafo.btviewer.sql.query.ValueType;
import org.jetbrains.annotations.NotNull;

public class LiteralValues {

    public static Value from(@NotNull SqlToken token) {
        Value value;
        if (token.getTokenType() == SqlTokenType.NUMBER) {
            value = new Value(token.getValue(), ValueType.NUMBER);
        } else if (token.getTokenType() == SqlTokenType.QUOTED_STRING) {
            value = new Value(token.getUnquotedValue(), ValueType.STRING);
        } else if (token.getTokenType() == SqlTokenType.FUNCTION_EXPRESSION) {
            value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Expected a number, quoted string or a function expression but was %s", token.getValue()));
        }

        return value;
    }
}
