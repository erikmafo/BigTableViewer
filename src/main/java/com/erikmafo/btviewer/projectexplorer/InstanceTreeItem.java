package com.erikmafo.btviewer.projectexplorer;

import com.erikmafo.btviewer.services.ListTablesService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TreeItem;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class InstanceTreeItem extends TreeItem<TreeItemData> {

    private final ListTablesService listTablesService;
    private final BooleanProperty loadingProperty;

    private boolean loadedChildren;

    @Inject
    public InstanceTreeItem(ListTablesService listTablesService) {
        this.loadingProperty = new SimpleBooleanProperty(false);
        this.listTablesService = listTablesService;
        this.expandedProperty().addListener((observable, prev, isExpanded) -> {
            if (isExpanded && !loadedChildren && !listTablesService.isRunning()) {
                loadChildren();
            }
        });
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public ReadOnlyBooleanProperty loadingProperty() {
        return loadingProperty;
    }

    private void loadChildren() {
        listTablesService.setInstance(getValue().toInstance());
        loadingProperty.setValue(true);
        listTablesService.setOnSucceeded(event -> {
            loadingProperty.set(false);
            var children = listTablesService
                    .getValue()
                    .stream()
                    .map(TreeItemData::new)
                    .map(this::createTableTreeItem)
                    .collect(Collectors.toList());
            getChildren().setAll(children);
            loadedChildren = true;
        });
        listTablesService.setOnFailed(event -> {
            loadingProperty.set(false);
            // TODO: show error
        });
        listTablesService.restart();
    }

    private TableTreeItem createTableTreeItem(TreeItemData treeItemData) {
        var tableTreeItem = new TableTreeItem();
        tableTreeItem.setValue(treeItemData);
        return tableTreeItem;
    }
}
