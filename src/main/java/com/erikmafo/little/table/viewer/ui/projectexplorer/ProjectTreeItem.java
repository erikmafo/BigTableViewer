package com.erikmafo.little.table.viewer.ui.projectexplorer;

import com.erikmafo.little.table.viewer.services.instance.LoadInstancesService;
import com.erikmafo.little.table.viewer.util.AlertUtil;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.stream.Collectors;

public class ProjectTreeItem extends TreeItem<TreeItemData> {

    @NotNull
    private final LoadInstancesService loadInstancesService;
    private final Provider<InstanceTreeItem> instanceTreeItemProvider;

    private boolean loadedChildren;

    @Inject
    public ProjectTreeItem(@NotNull LoadInstancesService loadInstancesService,
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

    @NotNull
    private InstanceTreeItem createChild(@NotNull TreeItemData treeItemData) {
        var instanceItem = instanceTreeItemProvider.get();
        instanceItem.setValue(treeItemData);
        treeItemData.setTreeItem(instanceItem);
        return instanceItem;
    }
}
