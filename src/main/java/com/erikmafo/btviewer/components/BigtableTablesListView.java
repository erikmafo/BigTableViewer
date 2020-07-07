package com.erikmafo.btviewer.components;
import com.erikmafo.btviewer.FXMLLoaderUtil;
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

public class BigtableTablesListView extends VBox {

    private static final String PROJECTS = "Projects";

    @FXML
    private Button addInstanceButton;

    @FXML
    private TreeView<String> treeView;

    private SimpleObjectProperty<BigtableTable> selectedTableProperty;

    private EventHandler<ProjectTreeItemExpanded> treeItemExpandedHandler;

    public BigtableTablesListView(){

        FXMLLoaderUtil.loadFxml("/fxml/bigtable_tables_list_view.fxml", this);

        treeView.setRoot(new TreeItem<>(PROJECTS));
        treeView.getRoot().setExpanded(true);
        selectedTableProperty = new SimpleObjectProperty<>();
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, prevSelected, selectedItem) -> {
            if (isTable(selectedItem)) {
                var tableId = selectedItem.getValue();
                var instanceId = selectedItem.getParent().getValue();
                var projectId = selectedItem.getParent().getParent().getValue();
                selectedTableProperty.set(new BigtableTable(projectId, instanceId, tableId));
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
        var projectTreeItem = findTreeItem(projectId).orElse(createProjectTreeItem(projectId));
        var instanceTreeItem = new TreeItem<>(instance.getInstanceId());
        projectTreeItem.getChildren().add(instanceTreeItem);
        treeView.getRoot().getChildren().add(projectTreeItem);
        treeView.setVisible(true);
    }

    private TreeItem<String> createProjectTreeItem(String projectId) {
        var projectTreeItem = new TreeItem<>(projectId);
        projectTreeItem.expandedProperty().addListener((observableValue, wasExpanded, isExpanded) -> {
            if (isExpanded) {
                fireTreeItemExpandedEvent(projectTreeItem);
            }
        });
        return projectTreeItem;
    }

    private void fireTreeItemExpandedEvent(TreeItem<String> projectTreeItem) {
        if (treeItemExpandedHandler != null) {
            treeItemExpandedHandler.handle(new ProjectTreeItemExpanded(getInstances(projectTreeItem)));
        }
    }

    private Optional<TreeItem<String>> findTreeItem(String projectId) {
        return treeView.getRoot()
                .getChildren()
                .stream().filter(p -> p.getValue().equals(projectId))
                .findFirst();
    }

    private List<BigtableInstance> getInstances(TreeItem<String> projectTreeItem) {
        var projectId = projectTreeItem.getValue();
        return projectTreeItem.getChildren()
                .stream()
                .map(treeItem -> new BigtableInstance(projectId, treeItem.getValue()))
                .collect(Collectors.toList());
    }

    public ReadOnlyObjectProperty<BigtableTable> selectedTableProperty() {
        return selectedTableProperty;
    }

    public void addBigtableTables(List<BigtableTable> tables) {
        tables.forEach(table -> treeView.getRoot()
                .getChildren()
                .stream()
                .filter(item -> item.getValue().equals(table.getProjectId()))
                .flatMap(item -> item.getChildren().stream())
                .filter(item -> item.getValue().equals(table.getInstanceId()))
                .findFirst()
                .ifPresent(instance -> addTableIfNotPresent(table, instance)));
    }

    private void addTableIfNotPresent(BigtableTable table, TreeItem<String> instance) {
        if (instance.getChildren().stream().noneMatch(c -> c.getValue().equals(table.getTableId()))) {
            instance.getChildren().add(new TreeItem<>(table.getTableId()));
        }
    }

    public void setTreeItemExpandedHandler(EventHandler<ProjectTreeItemExpanded> treeItemExpandedHandler) {
        this.treeItemExpandedHandler = treeItemExpandedHandler;
    }

    private static int countParents(TreeItem<String> treeItem) {
        return treeItem.getParent() == null ? 0 : 1 + countParents(treeItem.getParent());
    }

    private boolean isTable(TreeItem<String> selectedTreeItem) {
        return countParents(selectedTreeItem) == 3;
    }
}
