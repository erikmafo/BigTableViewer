package com.erikmafo.btviewer.sql;

import java.util.regex.Pattern;

public class SqlTokenizer {

    private static final char QUOTE = '\'';
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^([A-Za-z]+\\w*)(\\.[A-Za-z]+\\w*)?\\b");
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^(0|[1-9][0-9]*)\\b");
    private static final Pattern BOOL_PATTERN = Pattern.compile("^(?i)(true|false)\\b");

    private final String sql;

    private int position;

    public SqlTokenizer(String sql) {
        this.sql = sql;
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
            var indexOfNextQuote = sql.indexOf(QUOTE, position + 1);
            if (indexOfNextQuote < 0) {
                position = sql.length();
                return new SqlToken(remainingSql, SqlTokenType.INVALID, position, String.format("Expected a matching '%s'", QUOTE));
            }
            var quotedString = sql.substring(position, indexOfNextQuote + 1);
            position += quotedString.length();
            return new SqlToken(quotedString, SqlTokenType.QUOTED_STRING, position);
        }

        for (var word : ReservedWord.values()) {
            if (word.matchesStartOf(remainingSql)) {
                position += word.length();
                if (!word.isSupported()) {
                    return new SqlToken(word.value(), SqlTokenType.INVALID, position, String.format("'%s' is not supported", word.value()));
                }
                return new SqlToken(word.value(), word.tokenType(), position);
            }
        }

        var matcher = IDENTIFIER_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            var token = remainingSql.substring(matcher.start(), matcher.end());
            position += token.length();
            return new SqlToken(token, SqlTokenType.IDENTIFIER, position);
        }

        matcher = BOOL_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            var token = remainingSql.substring(matcher.start(), matcher.end());
            position += token.length();
            return new SqlToken(token, SqlTokenType.BOOL, position);
        }

        matcher = INTEGER_PATTERN.matcher(remainingSql);
        if (matcher.find()) {
            var token = remainingSql.substring(matcher.start(), matcher.end());
            position += token.length();
            return new SqlToken(token, SqlTokenType.INTEGER, position);
        }

        position = sql.length();
        return new SqlToken(remainingSql, SqlTokenType.INVALID, position, String.format("Unable to parse: '%s'", remainingSql));
    }
}
