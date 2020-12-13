package com.erikmafo.btviewer.sql;
import com.erikmafo.btviewer.sql.functions.Function;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SqlTokenizer {

    public static final char QUOTE = '\'';
    public static final char OPENING_PARENTHESES = '(';
    public static final char CLOSING_PARENTHESES = ')';
    public static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^([A-Za-z]+\\w*)(\\.[A-Za-z]+\\w*)?\\b");
    public static final Pattern INTEGER_PATTERN = Pattern.compile("^(0|[1-9][0-9]*)\\b");
    public static final Pattern BOOL_PATTERN = Pattern.compile("^(?i)(true|false)\\b");

    private final String sql;
    private int position;

    public SqlTokenizer(String sql) {
        this.sql = sql;
    }

    public SqlTokenizer(String sql, int skip, int take) {
        this.sql = sql.substring(0, skip + take);
        this.position = skip;
    }

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

    @NotNull
    private SqlToken nextReservedWord(ReservedWord word) {
        position += word.length();
        if (!word.isSupported()) {
            return new SqlToken(word.value(), SqlTokenType.INVALID, position, null, String.format("'%s' is not supported", word.value()));
        }
        return new SqlToken(word.value(), word.tokenType(), position);
    }

    @NotNull
    private SqlToken next(String remainingSql, int start, int end, SqlTokenType tokenType) {
        var token = remainingSql.substring(start, end);
        position += token.length();
        return new SqlToken(token, tokenType, position);
    }

    @NotNull
    private SqlToken nextFuncExpression(Function functionName, String remainingSql) {
        var funcExpression = functionName.extractExpression(remainingSql);
        var subTokens = new ArrayList<SqlToken>();
        subTokens.add(new SqlToken(functionName.value(), SqlTokenType.FUNCTION_NAME, position + functionName.length()));
        var skip = position + functionName.length();
        var take = funcExpression.length() - functionName.length();
        var funcExpressionSkipNameTokenizer = new SqlTokenizer(sql, skip, take);
        subTokens.addAll(funcExpressionSkipNameTokenizer.all());
        position += funcExpression.length();
        return new SqlToken(funcExpression, SqlTokenType.FUNCTION_EXPRESSION, position, subTokens);
    }
}
