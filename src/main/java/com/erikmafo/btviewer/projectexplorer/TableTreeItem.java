package com.erikmafo.btviewer.projectexplorer;

import javafx.scene.control.TreeItem;

public class TableTreeItem extends TreeItem<TreeItemData> {

    @Override
    public boolean isLeaf() {
        return true;
    }
}
