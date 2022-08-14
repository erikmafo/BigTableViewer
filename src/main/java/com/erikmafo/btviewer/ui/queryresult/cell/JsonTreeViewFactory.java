package com.erikmafo.btviewer.ui.queryresult.cell;

import com.erikmafo.btviewer.util.Check;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Factory class used to create a {@link TreeView<String>} representing json data.
 */
public class JsonTreeViewFactory {

    /**
     * Use a fixed cell size in order to be able to dynamically update {@link TreeView#prefHeightProperty()} according
     * to {@link TreeView#expandedItemCountProperty()}.
     */
    private static final double FIXED_CELL_SIZE = 24;
    private static final double MIN_PREF_HEIGHT = 2;
    private static final String UNDEFINED_VALUE = "Undefined";

    /**
     * Creates a {@link TreeView<String>} from json data. The height of the {@link TreeView} will adjust automatically
     * according to the number of expanded items.
     * @param json the json data.
     * @return A {@link TreeView<String>} that represents the json data.
     */
    @NotNull
    @Contract(pure = true)
    public static TreeView<String> createTreeView(@NotNull String json) {
        Check.notNull(json, "json");

        var treeView = new TreeView<>(createTreeItem(getJsonElement(json)));
        treeView.setFixedCellSize(FIXED_CELL_SIZE);
        treeView.setShowRoot(false);
        treeView.setPrefHeight(getPrefHeight(treeView.getExpandedItemCount()));
        treeView.expandedItemCountProperty().addListener((observable, oldVal, newVal) ->
                Platform.runLater(() -> treeView.setPrefHeight(getPrefHeight(treeView.getExpandedItemCount()))));

        return treeView;
    }

    @NotNull
    private static TreeItem<String> createTreeItem(@NotNull JsonElement jsonElement, String name) {

        var treeItem = new TreeItem<String>();

        if (jsonElement.isJsonArray()) {
            treeItem.setValue(name);
            treeItem.getChildren().addAll(createTreeItemForEachElement(jsonElement.getAsJsonArray()));
        } else if (jsonElement.isJsonObject()) {
            treeItem.setValue(name);
            treeItem.getChildren().addAll(createTreeItemForEachProperty(jsonElement.getAsJsonObject()));
        } else if (jsonElement.isJsonPrimitive()) {
            treeItem.setValue(getPrimitiveValue(jsonElement, name));
        } else if (jsonElement.isJsonNull()) {
            treeItem.setValue(getNullValue(name));
        } else {
            treeItem.setValue(UNDEFINED_VALUE);
        }

        return treeItem;
    }

    @NotNull
    private static String getNullValue(@NotNull String name) {
        return String.format("%s: null", name);
    }

    @NotNull
    private static TreeItem<String> createTreeItem(@NotNull JsonElement jsonElement) {
        return createTreeItem(jsonElement, null);
    }

    @NotNull
    private static Collection<TreeItem<String>> createTreeItemForEachProperty(@NotNull JsonObject jsonObj) {
        var treeItems = new ArrayList<TreeItem<String>>();
        for (var prop : jsonObj.entrySet()) {
            treeItems.add(createTreeItem(prop.getValue(), prop.getKey()));
        }
        return treeItems;
    }

    @NotNull
    private static Collection<TreeItem<String>> createTreeItemForEachElement(@NotNull JsonArray jsonArray) {
        var treeItems = new ArrayList<TreeItem<String>>();
        for (var i = 0; i < jsonArray.size(); i++) {
            treeItems.add(createTreeItem(jsonArray.get(i), String.format("%d", i)));
        }
        return treeItems;
    }

    @NotNull
    private static String getPrimitiveValue(@NotNull JsonElement jsonElement, String name) {
        return String.format("%s: %s", name, jsonElement.getAsString());
    }

    @NotNull
    private static JsonElement getJsonElement(String json) {
        return JsonParser.parseString(json);
    }

    private static double getPrefHeight(@NotNull Number expandedItems) {
        return MIN_PREF_HEIGHT + FIXED_CELL_SIZE * expandedItems.doubleValue();
    }
}
