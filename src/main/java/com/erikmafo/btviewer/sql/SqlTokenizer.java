package com.erikmafo.btviewer.sql;

import com.erikmafo.btviewer.sql.functions.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
     * Returns the next {@link SqlToken} in the sql query string.
     *
     * @return a SqlToken, or null if there are no more tokens.
     */
    public SqlToken next() {
        if (position >= sql.length()) {
            return null;
        }

        if (Character.isWhitespace(sql.charAt(position))) {
            position += 1;
            return next();
        }

        var remainingSql = sql.substring(position);

        if (sql.charAt(position) == QUOTE) {
            return nextQuotedString(remainingSql);
        }

        if (sql.charAt(position) == OPENING_PARENTHESES) {
            position += 1;
            return new SqlToken("" + OPENING_PARENTHESES, SqlTokenType.OPENING_PARENTHESES, position);
        }

        if (sql.charAt(position) == CLOSING_PARENTHESES) {
            position += 1;
            return new SqlToken("" + CLOSING_PARENTHESES, SqlTokenType.CLOSING_PARENTHESES, position);
        }

        for (var word : ReservedWord.values()) {
            if (word.matchesStartOf(remainingSql)) {
                return nextReservedWord(word);
            }
        }

        for (var func : Function.values()) {
            if (func.matchesStartOf(remainingSql)) {
                return nextFuncExpression(func, remainingSql);
            }
        }

        var matcher = IDENTIFIER_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            return next(remainingSql, matcher.start(), matcher.end(), SqlTokenType.IDENTIFIER);
        }

        matcher = BOOL_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            return next(remainingSql, matcher.start(), matcher.end(), SqlTokenType.BOOL);
        }

        matcher = INTEGER_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            return next(remainingSql, matcher.start(), matcher.end(), SqlTokenType.INTEGER);
        }

        position = sql.length();
        return new SqlToken(remainingSql, SqlTokenType.INVALID, position, null, String.format("Unable to parse: '%s'", remainingSql));
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

    @NotNull
    private SqlToken nextQuotedString(String remainingSql) {
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
    private SqlToken nextFuncExpression(Function functionName, String remainingSql) {
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
    private String extractExpression(@NotNull Function function, String sql) {
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
