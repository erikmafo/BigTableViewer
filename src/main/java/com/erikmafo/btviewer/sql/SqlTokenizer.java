package com.erikmafo.btviewer.sql;

import com.erikmafo.btviewer.sql.functions.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Used to break a string into tokens. Each token is represented by a {@link SqlToken}.
 */
public class SqlTokenizer {

    private static final char QUOTE = '\'';
    private static final char OPENING_PARENTHESES = '(';
    private static final char CLOSING_PARENTHESES = ')';
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^([A-Za-z]+\\w*)(\\.[A-Za-z]+\\w*)?\\b");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^(0|[1-9][0-9]*)\\b");
    private static final Pattern BOOL_PATTERN = Pattern.compile("^(?i)(true|false)\\b");

    private final String sql;
    private int position;

    /**
     * Creates a new tokenizer from the given sql query.
     * @param sql the sql query to tokenize.
     */
    public SqlTokenizer(String sql) {
        this.sql = sql;
    }

    private SqlTokenizer(@NotNull String sql, int skip, int take) {
        this.sql = sql.substring(0, skip + take);
        this.position = skip;
    }

    /**
     * Returns a list of all the tokens in the query string.
     *
     * @return a list of {@link SqlToken}'s.
     */
    public List<SqlToken> all() {
        var tokens = new ArrayList<SqlToken>();
        SqlToken current = next();
        while (current != null) {
            tokens.add(current);
            current = next();
        }

        return tokens;
    }

    /**
     * Returns the next {@link SqlToken} in the sql query string.
     *
     * @return a {@link SqlToken}, or null if there are no more tokens.
     */
    public SqlToken next() {
        return getNextToken(
                this::ignoreWhitespace,
                this::readQuote,
                this::readOpeningParentheses,
                this::readClosingParentheses,
                this::readReservedWord,
                this::readFunction,
                this::readIdentifier,
                this::readBool,
                this::readInteger);
    }

    @SafeVarargs
    @Nullable
    private SqlToken getNextToken(@NotNull java.util.function.Function<String, SqlToken>... tokenReaders) {

        if (position >= sql.length()) {
            return null;
        }

        var remainingSql = sql.substring(position);

        for (var reader : tokenReaders) {
            var token = reader.apply(remainingSql);
            if (token != null) {
                return token;
            }
        }

        position = sql.length();
        return getInvalidToken(remainingSql);
    }

    @Nullable
    private SqlToken ignoreWhitespace(@NotNull String remainingSql) {
        if (Character.isWhitespace(remainingSql.charAt(0))) {
            position += 1;
            return next();
        }

        return null;
    }

    @Nullable
    private SqlToken readOpeningParentheses(@NotNull String remainingSql) {
        if (remainingSql.charAt(0) == OPENING_PARENTHESES) {
            position += 1;
            return new SqlToken("" + OPENING_PARENTHESES, SqlTokenType.OPENING_PARENTHESES, position);
        }
        return null;
    }

    @Nullable
    private SqlToken readClosingParentheses(@NotNull String remainingSql) {
        if (remainingSql.charAt(0) == CLOSING_PARENTHESES) {
            position += 1;
            return new SqlToken("" + CLOSING_PARENTHESES, SqlTokenType.CLOSING_PARENTHESES, position);
        }
        return null;
    }

    @NotNull
    private SqlToken getInvalidToken(@NotNull String remainingSql) {
        return new SqlToken(remainingSql, SqlTokenType.INVALID, position, null, String.format("Unable to parse: '%s'", remainingSql));
    }

    @Nullable
    private SqlToken readQuote(@NotNull String remainingSql) {
        if (remainingSql.charAt(0) == QUOTE) {
            return nextQuotedString(remainingSql);
        }
        return null;
    }

    @Nullable
    private SqlToken readInteger(@NotNull String remainingSql) {
        var matcher = INTEGER_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            return next(remainingSql, matcher.start(), matcher.end(), SqlTokenType.INTEGER);
        }
        return null;
    }

    @Nullable
    private SqlToken readBool(@NotNull String remainingSql) {
        var matcher = BOOL_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            return next(remainingSql, matcher.start(), matcher.end(), SqlTokenType.BOOL);
        }
        return null;
    }

    @Nullable
    private SqlToken readIdentifier(@NotNull String remainingSql) {
        var matcher = IDENTIFIER_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            return next(remainingSql, matcher.start(), matcher.end(), SqlTokenType.IDENTIFIER);
        }
        return null;
    }

    @Nullable
    private SqlToken readFunction(@NotNull String remainingSql) {
        for (var func : Function.values()) {
            if (func.matchesStartOf(remainingSql)) {
                return nextFuncExpression(func, remainingSql);
            }
        }
        return null;
    }

    @Nullable
    private SqlToken readReservedWord(@NotNull String remainingSql) {
        for (var word : ReservedWord.values()) {
            if (word.matchesStartOf(remainingSql)) {
                return nextReservedWord(word);
            }
        }
        return null;
    }

    @NotNull
    private SqlToken nextQuotedString(@NotNull String remainingSql) {
        var indexOfNextQuote = sql.indexOf(QUOTE, position + 1);
        if (indexOfNextQuote < 0) {
            position = sql.length();
            return new SqlToken(remainingSql, SqlTokenType.INVALID, position, null, String.format("Expected a matching '%s'", QUOTE));
        }
        return next(sql, position, indexOfNextQuote + 1, SqlTokenType.QUOTED_STRING);
    }

    @Contract("_ -> new")
    @NotNull
    private SqlToken nextReservedWord(@NotNull ReservedWord word) {
        position += word.length();
        if (!word.isSupported()) {
            return new SqlToken(word.value(), SqlTokenType.INVALID, position, null, String.format("'%s' is not supported", word.value()));
        }
        return new SqlToken(word.value(), word.tokenType(), position);
    }

    @Contract("_, _, _, _ -> new")
    @NotNull
    private SqlToken next(@NotNull String remainingSql, int start, int end, SqlTokenType tokenType) {
        var token = remainingSql.substring(start, end);
        position += token.length();
        return new SqlToken(token, tokenType, position);
    }

    @NotNull
    private SqlToken nextFuncExpression(@NotNull Function functionName, @NotNull String remainingSql) {
        var funcExpression = extractExpression(functionName, remainingSql);
        var subTokens = new ArrayList<SqlToken>();
        subTokens.add(new SqlToken(functionName.value(), SqlTokenType.FUNCTION_NAME, position + functionName.length()));
        var skip = position + functionName.length();
        var take = funcExpression.length() - functionName.length();
        var funcExpressionSkipNameTokenizer = new SqlTokenizer(sql, skip, take);
        subTokens.addAll(funcExpressionSkipNameTokenizer.all());
        position += funcExpression.length();
        return new SqlToken(funcExpression, SqlTokenType.FUNCTION_EXPRESSION, position, subTokens);
    }

    @NotNull
    private String extractExpression(@NotNull Function function, @NotNull String sql) {
        if (!function.matchesStartOf(sql)) {
            throw new IllegalArgumentException(sql + "does not start with a function expression of type " + this);
        }

        var numberOfOpeningParentheses = 1;
        var numberOfClosingParentheses = 0;
        var index = sql.indexOf("(") + 1;
        while (index < sql.length()) {
            var ch = sql.charAt(index);
            if (ch == '(') {
                numberOfOpeningParentheses++;
            } else if (ch == ')') {
                numberOfClosingParentheses++;
            }
            if (numberOfClosingParentheses == numberOfOpeningParentheses) {
                break;
            }
            index++;
        }

        return sql.substring(0, index + 1);
    }
}
