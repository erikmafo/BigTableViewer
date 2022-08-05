package com.erikmafo.ltviewer.ui.queryresult.cell;

import com.erikmafo.ltviewer.model.BigtableValue;
import com.erikmafo.ltviewer.model.ValueTypeConstants;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CellContentFactory {

    @Nullable
    public static Node getContent(BigtableValue bigtableValue) {

        if (bigtableValue == null) {
            return null;
        }

        var stringValue = bigtableValue.asString();

        return isComplexObject(bigtableValue.getType())
                ? JsonTreeViewFactory.createTreeView(stringValue)
                : toLabel(stringValue);
    }

    @NotNull
    private static Label toLabel(@NotNull String s) {
        var label = new Label();
        label.setText(s);
        return label;
    }

    private static boolean isComplexObject(@NotNull String valueType) {
        switch (valueType) {
            case ValueTypeConstants.PROTO:
            case ValueTypeConstants.JSON:
                return true;
            default:
                return false;
        }
    }
}
