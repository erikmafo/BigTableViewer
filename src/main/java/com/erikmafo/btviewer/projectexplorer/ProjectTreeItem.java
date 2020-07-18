package com.erikmafo.btviewer.projectexplorer;

import com.erikmafo.btviewer.services.LoadInstancesService;
import com.erikmafo.btviewer.util.AlertUtil;
import javafx.concurrent.WorkerStateEvent;
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
        expandedProperty().addListener((observable, prev, isExpanded) -> {
            if (isExpanded && !loadedChildren && !loadInstancesService.isRunning()) {
                loadChildren();
            }
        });
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public void loadChildren() {
        loadInstancesService.setProjectId(getValue().getProjectId());
        loadInstancesService.setOnSucceeded(this::onLoadInstancesSucceeded);
        loadInstancesService.setOnFailed(event -> AlertUtil.displayError("Unable to load instances", event));
        loadInstancesService.restart();
    }

    private void onLoadInstancesSucceeded(WorkerStateEvent event) {
        var children = loadInstancesService
                .getValue()
                .stream()
                .map(TreeItemData::new)
                .map(this::createChild)
                .collect(Collectors.toList());
        getChildren().setAll(children);
        loadedChildren = true;
    }

    private InstanceTreeItem createChild(TreeItemData treeItemData) {
        var instanceItem = instanceTreeItemProvider.get();
        instanceItem.setValue(treeItemData);
        return instanceItem;
    }
}
