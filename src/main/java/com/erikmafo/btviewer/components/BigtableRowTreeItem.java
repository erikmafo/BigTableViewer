package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.model.BigtableRow;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BigtableRowTreeItem extends TreeItem<BigtableRow> {

    private boolean isLeaf;

    // used to cache the result of isLeaf
    private boolean isFirstTimeLeaf = true;
    // used to cache the result of get children
    private boolean isFirstTimeChildren = true;

    public BigtableRowTreeItem(BigtableRow row) {
        super(row);
        this.isLeaf = false;
    }

    public BigtableRowTreeItem(BigtableRow row, boolean isLeaf) {
        super(row);
        this.isLeaf = isLeaf;
        this.isFirstTimeLeaf = false;
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            isLeaf = getValue() != null && getValue().getPreviousVersion() == null;
        }

        return isLeaf;
    }

    @Override
    public ObservableList<TreeItem<BigtableRow>> getChildren() {

        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            if (!isRoot()) {
                getChildren().setAll(buildChildrenList());
            }
        }

        return super.getChildren();
    }

    private boolean isRoot() {
        return getValue() == null;
    }

    @NotNull
    private List<BigtableRowTreeItem> buildChildrenList() {

        if (isLeaf) {
            return Collections.emptyList();
        }

        return getValue()
                .getPreviousVersions()
                .stream()
                .map(BigtableRowTreeItem::createChild)
                .collect(Collectors.toList());
    }

    private static BigtableRowTreeItem createChild(BigtableRow row) {
        return new BigtableRowTreeItem(row, true);
    }
}
