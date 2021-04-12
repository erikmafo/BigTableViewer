package com.erikmafo.btviewer.ui.querybox;

import com.erikmafo.btviewer.sql.SqlToken;
import com.erikmafo.btviewer.sql.SqlTokenType;
import com.erikmafo.btviewer.sql.SqlTokenizer;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SyntaxHighlightingUtil {

    private static final String KEYWORD_STYLE_CLASS = "keyword";
    private static final String STRING_STYLE_CLASS = "string";
    private static final String FUNCTION_STYLE_CLASS = "function";
    private static final String OPERATOR_STYLE_CLASS = "logical-operator";

    public static StyleSpans<Collection<String>> computeSyntaxHighlighting(@NotNull String queryText) {
        var sqlTokenizer = new SqlTokenizer(queryText);
        var token = sqlTokenizer.next();
        int lastHighlightEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (token != null) {
            for (var t : getTokenAndSubTokens(token)) {
                lastHighlightEnd = applyStyleClass(t, lastHighlightEnd, spansBuilder);
            }
            token = sqlTokenizer.next();
        }
        spansBuilder.add(Collections.emptyList(),queryText.length() - lastHighlightEnd);
        return spansBuilder.create();
    }

    private static int applyStyleClass(@NotNull SqlToken token, int lastHighlightEnd, @NotNull StyleSpansBuilder<Collection<String>> spansBuilder) {
        var styleClass = getStyleClass(token);
        if (styleClass != null) {
            spansBuilder.add(Collections.emptyList(), token.getStart() - lastHighlightEnd);
            spansBuilder.add(Collections.singleton(styleClass), token.getValue().length());
            lastHighlightEnd = token.getEnd();
        }
        return lastHighlightEnd;
    }

    @NotNull
    private static List<SqlToken> getTokenAndSubTokens(@NotNull SqlToken token) {
        var tokens = new ArrayList<SqlToken>();
        tokens.add(token);
        if (token.getSubTokens() != null) {
            for (var subToken : token.getSubTokens()) {
                tokens.addAll(getTokenAndSubTokens(subToken));
            }
        }
        return tokens;
    }

    @Nullable
    private static String getStyleClass(@NotNull SqlToken token) {
        if (SqlTokenType.QUOTED_STRING.equals(token.getTokenType())) {
            return STRING_STYLE_CLASS;
        }

        if (SqlTokenType.FUNCTION_NAME.equals(token.getTokenType())) {
            return FUNCTION_STYLE_CLASS;
        }

        if (SqlTokenType.OPERATOR.equals(token.getTokenType())) {
            return OPERATOR_STYLE_CLASS;
        }

        switch (token.getValue().toUpperCase()) {
            case "SELECT":
            case "WHERE":
            case "FROM":
            case "LIMIT":
            case "KEY":
            case "TIMESTAMP":
                return KEYWORD_STYLE_CLASS;
            case "AND":
                return OPERATOR_STYLE_CLASS;
            default:
                return null;
        }
    }
}
