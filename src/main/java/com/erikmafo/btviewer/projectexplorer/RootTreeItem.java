package com.erikmafo.btviewer.projectexplorer;

import com.erikmafo.btviewer.services.LoadProjectsService;
import com.google.inject.Provider;
import javafx.scene.control.TreeItem;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class RootTreeItem extends TreeItem<TreeItemData> {

    private final LoadProjectsService loadProjectsService;
    private final Provider<ProjectTreeItem> projectTreeItemProvider;

    @Inject
    public RootTreeItem(
            LoadProjectsService loadProjectsService,
            Provider<ProjectTreeItem> projectTreeItemProvider) {
        this.projectTreeItemProvider = projectTreeItemProvider;
        this.loadProjectsService = loadProjectsService;
        this.loadProjectsService.setOnSucceeded(event -> {
            var children = loadProjectsService
                    .getValue()
                    .stream()
                    .map(TreeItemData::new)
                    .map(this::createProjectTreeItem)
                    .collect(Collectors.toList());
            getChildren().setAll(children);
        });
        this.loadProjectsService.setOnFailed(event -> {
            // TODO: show error
        });
        this.loadProjectsService.restart();
        this.setExpanded(true);
        setValue(new TreeItemData());
    }

    private ProjectTreeItem createProjectTreeItem(TreeItemData info) {
        var treeItem = projectTreeItemProvider.get();
        treeItem.setValue(info);
        return treeItem;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
