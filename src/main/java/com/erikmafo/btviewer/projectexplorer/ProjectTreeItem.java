package com.erikmafo.btviewer.projectexplorer;

import com.erikmafo.btviewer.services.LoadInstancesService;
import javafx.scene.control.TreeItem;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.stream.Collectors;

public class ProjectTreeItem extends TreeItem<TreeItemData> {

    private final LoadInstancesService loadInstancesService;
    private final Provider<InstanceTreeItem> instanceTreeItemProvider;

    private boolean loadedChildren;

    @Inject
    public ProjectTreeItem(LoadInstancesService loadInstancesService,
                           Provider<InstanceTreeItem> instanceTreeItemProvider) {
        this.loadInstancesService = loadInstancesService;
        this.instanceTreeItemProvider = instanceTreeItemProvider;
        this.expandedProperty().addListener((observable, prev, isExpanded) -> {
            if (isExpanded && !loadedChildren && !loadInstancesService.isRunning()) {
                loadChildren();
            }
        });
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    private void loadChildren() {
        loadInstancesService.setProjectId(getValue().getProjectId());
        loadInstancesService.setOnSucceeded(event -> {
            var children = loadInstancesService
                    .getValue()
                    .stream()
                    .map(TreeItemData::new)
                    .map(this::createInstanceTreeItem)
                    .collect(Collectors.toList());
            getChildren().setAll(children);
            loadedChildren = true;
        });
        loadInstancesService.setOnFailed(event -> {
            loadInstancesService.getException().printStackTrace();
            // TODO: show error
        });
        loadInstancesService.restart();
    }

    private InstanceTreeItem createInstanceTreeItem(TreeItemData treeItemData) {
        var instanceItem = instanceTreeItemProvider.get();
        instanceItem.setValue(treeItemData);
        return instanceItem;
    }
}
