package com.erikmafo.btviewer.ui.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.controlsfx.glyphfont.FontAwesome;
import org.jetbrains.annotations.NotNull;


public class ContextMenuUtil {

    @NotNull
    public static ContextMenu createMenu(MenuItem... items) {
        var contextMenu = new ContextMenu();
        contextMenu.setStyle("");
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);
        contextMenu.getItems().addAll(items);
        return contextMenu;
    }

    @NotNull
    public static MenuItem createCutMenuItem(EventHandler<ActionEvent> handler) {
        var menuItem = new MenuItem("Cut");
        menuItem.setGraphic(FontAwesomeUtil.create(FontAwesome.Glyph.CUT));
        menuItem.setOnAction(handler);
        return menuItem;
    }

    @NotNull
    public static MenuItem createCopyMenuItem(EventHandler<ActionEvent> handler) {
        var menuItem = new MenuItem("Copy");
        menuItem.setGraphic(FontAwesomeUtil.create(FontAwesome.Glyph.COPY));
        menuItem.setOnAction(handler);
        return menuItem;
    }

    @NotNull
    public static MenuItem createPasteMenuItem(EventHandler<ActionEvent> handler) {
        var menuItem = new MenuItem("Paste");
        menuItem.setGraphic(FontAwesomeUtil.create(FontAwesome.Glyph.PASTE));
        menuItem.setOnAction(handler);
        return menuItem;
    }
}
