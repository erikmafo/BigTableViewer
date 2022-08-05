package com.erikmafo.little.table.viewer.ui.projectexplorer;

import com.erikmafo.little.table.viewer.services.project.LoadProjectsService;
import com.erikmafo.little.table.viewer.util.AlertUtil;
import com.google.inject.Provider;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class RootTreeItem extends TreeItem<TreeItemData> {

    @NotNull
    private final LoadProjectsService loadProjectsService;
    private final Provider<ProjectTreeItem> projectTreeItemProvider;

    @Inject
    public RootTreeItem(
            @NotNull LoadProjectsService loadProjectsService,
            Provider<ProjectTreeItem> projectTreeItemProvider) {
        this.projectTreeItemProvider = projectTreeItemProvider;
        this.loadProjectsService = loadProjectsService;
        loadChildren(loadProjectsService);
        setExpanded(true);
        setValue(new TreeItemData());
    }

    public void reloadOrAddProject(String projectId) {
        getChildren()
                .stream()
                .filter(item -> item.getValue().getProjectId().equals(projectId))
                .findFirst()
                .ifPresentOrElse(
                        item -> ((ProjectTreeItem)item).loadChildren(),
                        () -> getChildren().add(createProjectTreeItem(new TreeItemData(projectId))));
    }

    public void removeProject(String projectId) {
        getChildren().removeIf(item -> item.getValue().getProjectId().equals(projectId));
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    private void loadChildren(@NotNull LoadProjectsService loadProjectsService) {
        this.loadProjectsService.setOnSucceeded(event -> {
            var children = loadProjectsService
                    .getValue()
                    .stream()
                    .map(TreeItemData::new)
                    .map(this::createProjectTreeItem)
                    .collect(Collectors.toList());
            getChildren().setAll(children);
        });
        this.loadProjectsService.setOnFailed(event -> AlertUtil.displayError("Failed to load projects", event));
        this.loadProjectsService.restart();
    }

    @NotNull
    private ProjectTreeItem createProjectTreeItem(TreeItemData info) {
        var treeItem = projectTreeItemProvider.get();
        treeItem.setValue(info);
        return treeItem;
    }
}
