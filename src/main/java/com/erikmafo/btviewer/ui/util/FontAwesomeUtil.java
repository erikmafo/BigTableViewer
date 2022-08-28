package com.erikmafo.btviewer.ui.util;

import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class FontAwesomeUtil {
    private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    public static Glyph create(Enum<?> glyph) {
        var glyphNode = fontAwesome.create(glyph);
        glyphNode.setStyle("-fx-font-family: FontAwesome;");
        return glyphNode;
    }
}
