package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.SqlToken;
import com.erikmafo.btviewer.sql.SqlTokenType;
import com.erikmafo.btviewer.sql.Value;
import com.erikmafo.btviewer.sql.ValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionExpression {

    public static Value evaluate(List<SqlToken> tokens) {
        var expression = new FunctionExpression();
        for (var token : tokens) {
            expression.handleNextToken(token);
        }
        return expression.evaluate();
    }

    enum Step {
        FUNCTION_TYPE,
        OPENING_PARENTHESES,
        INPUT,
        INPUT_COMMA,
        CLOSING_PARENTHESES,
        COMPLETE,
    }

    private Function function;
    private List<Value> args = new ArrayList<>();
    private Step step = Step.FUNCTION_TYPE;

    Value evaluate() {
        switch (function) {
            case REVERSE: return reverse(args);
            case CONCAT: return concat(args);
            default: throw new IllegalStateException(
                    String.format("The %s(...) function expression is not complete", function.value()));
        }
    }

    void handleNextToken(SqlToken token) {
        switch (step) {
            case FUNCTION_TYPE:
                functionType(token);
                break;
            case OPENING_PARENTHESES:
                openingParentheses(token);
                break;
            case INPUT:
                input(token);
                break;
            case INPUT_COMMA:
                inputComma(token);
                break;
            case CLOSING_PARENTHESES:
                closingParentheses(token);
                break;
            case COMPLETE:
                throw new IllegalArgumentException(
                        "Expected no more tokens in the function expression, but got: " + token.getValue());
        }
    }

    private void functionType(SqlToken token) {
        ensureExpectedToken(token, SqlTokenType.FUNCTION_NAME);
        function = Function.valueOf(token.getValue());
        step = Step.OPENING_PARENTHESES;
    }

    private void openingParentheses(SqlToken token) {
        ensureExpectedToken(token, SqlTokenType.OPENING_PARENTHESES);
        step = Step.INPUT;
    }

    private void input(SqlToken token) {
        args.add(Value.from(token));
        step= Step.INPUT_COMMA;
    }

    private void inputComma(SqlToken token) {
        if (token.getTokenType() == SqlTokenType.COMMA) {
            step = Step.INPUT;
        } else if (token.getTokenType() == SqlTokenType.CLOSING_PARENTHESES) {
            closingParentheses(token);
        } else {
            throwWrongTokenType(token, "Expected a comma or a closing parentheses");
        }
    }

    private void closingParentheses(SqlToken token) {
        ensureExpectedToken(token, SqlTokenType.CLOSING_PARENTHESES);
        step = Step.COMPLETE;
    }

    private void ensureExpectedToken(SqlToken token, SqlTokenType expectedType) {
        if (token.getTokenType() != expectedType) {
            throwWrongTokenType(token, String.format("Expected a %s", expectedType));
        }
    }

    private void throwWrongTokenType(SqlToken token, String expected) {
        throw new IllegalArgumentException(String.format("%s but was %s", expected, token.getValue()));
    }

    private Value concat(List<Value> args) {
        var stringValue = args.stream().map(Value::asString).collect(Collectors.joining(""));
        return new Value(stringValue, ValueType.STRING);
    }

    private Value reverse(List<Value> args) {
        if (args.size() != 1) {
            throw new IllegalArgumentException(String.format("%s(...) takes exactly one argument", Function.REVERSE));
        }
        var arg = args.get(0);
        if (arg.getValueType() != ValueType.STRING) {
            throw new IllegalArgumentException(String.format("%s(...) takes only string as argument", Function.REVERSE));
        }

        var reversed = new StringBuilder(arg.asString()).reverse().toString();
        return new Value(reversed, ValueType.STRING);
    }
}
