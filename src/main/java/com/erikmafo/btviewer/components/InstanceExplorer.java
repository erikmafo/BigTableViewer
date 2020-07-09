package com.erikmafo.btviewer.components;
import com.erikmafo.btviewer.FXMLLoaderUtil;
import com.erikmafo.btviewer.events.InstanceTreeItemExpanded;
import com.erikmafo.btviewer.events.ProjectTreeItemExpanded;
import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstanceExplorer extends VBox {

    private static final String PROJECTS = "Projects";

    @FXML
    private Button addInstanceButton;

    @FXML
    private TreeView<String> treeView;

    private SimpleObjectProperty<BigtableTable> selectedTableProperty;
    private SimpleObjectProperty<BigtableInstance> selectedInstanceProperty;
    private EventHandler<ProjectTreeItemExpanded> projectItemExpandedHandler;
    private EventHandler<InstanceTreeItemExpanded> instanceItemExpandedHandler;

    public InstanceExplorer(){

        FXMLLoaderUtil.loadFxml("/fxml/instance_explorer.fxml", this);

        treeView.setRoot(new TreeItem<>(PROJECTS));
        treeView.getRoot().setExpanded(true);
        selectedTableProperty = new SimpleObjectProperty<>();
        selectedInstanceProperty = new SimpleObjectProperty<>();

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, prevSelected, selectedItem) -> {
            if (isTable(selectedItem)) {
                var tableId = selectedItem.getValue();
                var instanceId = selectedItem.getParent().getValue();
                var projectId = selectedItem.getParent().getParent().getValue();
                var instanceItem = findTreeItem(findTreeItem(projectId).get(), instanceId);
                selectedInstanceProperty.set(new BigtableInstance(projectId, instanceId));
                selectedTableProperty.set(new BigtableTable(projectId, instanceId, tableId));
            } else if(isInstance(selectedItem)) {
                var instanceId = selectedItem.getValue();
                var projectId = selectedItem.getParent().getValue();
                var instance = new BigtableInstance(projectId, instanceId);
                selectedInstanceProperty.set(instance);
            }
        });
    }

    public void setOnCreateNewBigtableInstance(EventHandler<ActionEvent> eventHandler) {
        addInstanceButton.setOnAction(eventHandler);
    }

    public void addBigtableInstances(List<BigtableInstance> bigtableInstances) {
        for (var instance : bigtableInstances) {
            addBigtableInstance(instance);
        }
    }

    public void addBigtableInstance(BigtableInstance instance) {
        var projectId = instance.getProjectId();
        var projectTreeItem = findTreeItem(projectId).orElseGet(() -> createProjectTreeItem(projectId));
        var instanceTreeItem = createInstanceTreeItem(instance);
        projectTreeItem.getChildren().add(instanceTreeItem);
        treeView.setVisible(true);
    }

    private TreeItem<String> createProjectTreeItem(String projectId) {
        var projectTreeItem = new TreeItem<>(projectId);
        projectTreeItem.expandedProperty().addListener((observableValue, wasExpanded, isExpanded) -> {
            if (isExpanded) {
                fireProjectItemExpendedEvent(projectTreeItem);
            }
        });
        treeView.getRoot().getChildren().add(projectTreeItem);
        return projectTreeItem;
    }

    private TreeItem<String> createInstanceTreeItem(BigtableInstance instance) {
        var instanceItem = new TreeItem<>(instance.getInstanceId());
        instanceItem.expandedProperty().addListener((observableValue, wasExpanded, isExpanded) -> {
            if (isExpanded) {
                fireInstanceItemExpendedEvent(instance);
            }
        });
        return instanceItem;
    }

    private void fireProjectItemExpendedEvent(TreeItem<String> projectTreeItem) {
        if (projectItemExpandedHandler != null) {
            var projectItemExpanded =
                    new ProjectTreeItemExpanded(projectTreeItem.getValue(), getInstances(projectTreeItem));
            projectItemExpandedHandler.handle(projectItemExpanded);
        }
    }

    private void fireInstanceItemExpendedEvent(BigtableInstance instance) {
        if (instanceItemExpandedHandler != null) {
            instanceItemExpandedHandler.handle(new InstanceTreeItemExpanded(instance));
        }
    }

    private Optional<TreeItem<String>> findTreeItem(TreeItem<String> root, String value) {
        return root.getChildren()
            .stream()
            .filter(p -> p.getValue().equals(value))
            .findFirst();
    }

    private Optional<TreeItem<String>> findTreeItem(String projectId) {
        return findTreeItem(treeView.getRoot(), projectId);
    }

    private List<BigtableInstance> getInstances(TreeItem<String> projectTreeItem) {
        var projectId = projectTreeItem.getValue();
        return projectTreeItem.getChildren()
                .stream()
                .map(treeItem -> new BigtableInstance(projectId, treeItem.getValue()))
                .collect(Collectors.toList());
    }

    public ReadOnlyObjectProperty<BigtableInstance> selectedInstanceProperty() {
        return selectedInstanceProperty;
    }

    public ReadOnlyObjectProperty<BigtableTable> selectedTableProperty() {
        return selectedTableProperty;
    }

    public void setProjectItemExpandedHandler(EventHandler<ProjectTreeItemExpanded> projectItemExpandedHandler) {
        this.projectItemExpandedHandler = projectItemExpandedHandler;
    }

    public void setInstanceItemExpandedHandler(EventHandler<InstanceTreeItemExpanded> instanceItemExpandedHandler) {
        this.instanceItemExpandedHandler = instanceItemExpandedHandler;
    }

    public void addBigtableTables(List<BigtableTable> tables) {
        for (var table : tables) {
            addBigtableTable(table);
        }
    }

    private void addBigtableTable(BigtableTable table) {
        findTreeItem(table.getProjectId())
                .flatMap(project -> findTreeItem(project, table.getInstanceId()))
                .ifPresent(instance -> addTableToInstance(instance, table));
    }

    private void addTableToInstance(TreeItem<String> instance, BigtableTable table) {
        var tableAlreadyAdded = instance
                .getChildren()
                .stream()
                .anyMatch(t -> t.getValue().equals(table.getTableId()));

        if (!tableAlreadyAdded) {
            instance.getChildren().add(new TreeItem<>(table.getTableId()));
        }
    }

    private boolean isTable(TreeItem<String> selectedTreeItem) {
        return treeView.getTreeItemLevel(selectedTreeItem) == 3;
    }

    private boolean isInstance(TreeItem<String> selectedTreeItem) {
        return treeView.getTreeItemLevel(selectedTreeItem) == 2;
    }
}
