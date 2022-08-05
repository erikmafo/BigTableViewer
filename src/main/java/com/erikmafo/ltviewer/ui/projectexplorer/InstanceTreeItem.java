package com.erikmafo.ltviewer.ui.projectexplorer;

import com.erikmafo.ltviewer.services.table.ListTablesService;
import com.erikmafo.ltviewer.util.AlertUtil;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class InstanceTreeItem extends TreeItem<TreeItemData> {

    @NotNull
    private final ListTablesService listTablesService;
    private boolean loadedChildren;

    @Inject
    public InstanceTreeItem(@NotNull ListTablesService listTablesService) {
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

    public void loadChildren() {
        listTablesService.setInstance(getValue().toInstance());
        getValue().setLoading(true);
        listTablesService.setOnSucceeded(event -> {
            getValue().setLoading(false);
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
            loadedChildren = false;
            getValue().setLoading(false);
            AlertUtil.displayError("Failed to load tables", event);
        });
        listTablesService.restart();
    }

    @NotNull
    private TableTreeItem createTableTreeItem(TreeItemData treeItemData) {
        var tableTreeItem = new TableTreeItem();
        tableTreeItem.setValue(treeItemData);
        return tableTreeItem;
    }
}
