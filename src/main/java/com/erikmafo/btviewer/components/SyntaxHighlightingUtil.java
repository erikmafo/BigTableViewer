package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.sql.SqlToken;
import com.erikmafo.btviewer.sql.SqlTokenType;
import com.erikmafo.btviewer.sql.SqlTokenizer;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class SyntaxHighlightingUtil {

    private static final String KEYWORD_STYLE_CLASS = "keyword";
    private static final String STRING_STYLE_CLASS = "string";

    public static StyleSpans<Collection<String>> computeSyntaxHighlighting(String text) {
        var sqlTokenizer = new SqlTokenizer(text);
        var token = sqlTokenizer.next();
        int lastHighlightEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (token != null) {
            String styleClass = getStyleClass(token);
            if (styleClass != null) {
                spansBuilder.add(Collections.emptyList(),token.getStart() - lastHighlightEnd);
                spansBuilder.add(Collections.singleton(styleClass),token.getValue().length());
                lastHighlightEnd = token.getEnd();
            }
            token = sqlTokenizer.next();
        }
        spansBuilder.add(Collections.emptyList(),text.length() - lastHighlightEnd);
        return spansBuilder.create();
    }

    @Nullable
    private static String getStyleClass(SqlToken token) {
        if (SqlTokenType.QUOTED_STRING.equals(token.getTokenType())) {
            return STRING_STYLE_CLASS;
        }

        switch (token.getValue().toUpperCase()) {
            case "SELECT":
            case "WHERE":
            case "FROM":
            case "LIMIT":
            case "AND":
            case "LIKE":
            case "KEY":
                return KEYWORD_STYLE_CLASS;
            default:
                return null;
        }
    }
}
