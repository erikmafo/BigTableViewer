package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a part of a sql expression that involves a function call, e.g. COUNT(fieldName).
 */
public abstract class FunctionExpression {

    private enum Step {
        FUNCTION_TYPE,
        OPENING_PARENTHESES,
        INPUT,
        INPUT_COMMA,
        CLOSING_PARENTHESES,
        COMPLETE,
    }

    private Function function;
    private Step step = Step.FUNCTION_TYPE;

    protected Function getFunction() {
        return function;
    }

    /**
     * Handles the sql token that is input to the function.
     *
     * @param inputToken a sql token that represents the input to the function.
     */
    protected abstract void onInputToken(SqlToken inputToken);

    protected void read(@NotNull List<SqlToken> tokens) {
        for (var token : tokens) {
            handleNextToken(token);
        }
    }

    protected final void handleNextToken(@NotNull SqlToken token) {
        token.ensureValid();

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
        onInputToken(token);
        step= Step.INPUT_COMMA;
    }

    private void inputComma(@NotNull SqlToken token) {
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

    private void ensureExpectedToken(@NotNull SqlToken token, SqlTokenType expectedType) {
        if (token.getTokenType() != expectedType) {
            throwWrongTokenType(token, String.format("Expected a %s", expectedType));
        }
    }

    @Contract("_, _ -> fail")
    private void throwWrongTokenType(@NotNull SqlToken token, String expected) {
        throw new IllegalArgumentException(String.format("%s but was %s", expected, token.getValue()));
    }
}
