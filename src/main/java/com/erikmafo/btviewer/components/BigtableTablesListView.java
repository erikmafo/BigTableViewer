package com.erikmafo.btviewer.components;
import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class BigtableTablesListView extends GridPane {

    @FXML
    private Button addInstanceButton;

    @FXML
    private TreeView<String> treeView;

    private SimpleObjectProperty<BigtableTable> selectedTableProperty;

    private SimpleObjectProperty<BigtableInstance> selectedInstanceProperty = new SimpleObjectProperty<>();

    private SimpleStringProperty selectedProjectProperty = new SimpleStringProperty();

    public BigtableTablesListView(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bigtable_tables_list_view.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load fxml", e);
        }

        treeView.setRoot(new TreeItem<>("projects"));
        treeView.getRoot().setExpanded(true);
        selectedTableProperty = new SimpleObjectProperty<>();
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (countParents(newValue) == 3) {
                TreeItem<String> instanceId = newValue.getParent();
                TreeItem<String> projectId = instanceId.getParent();

                selectedTableProperty.set(new BigtableTable(
                        projectId.getValue(), instanceId.getValue(), newValue.getValue()));
            }
        });
    }

    private static int countParents(TreeItem<String> treeItem) {
        if (treeItem.getParent() == null) {
            return 0;
        }

        return 1 + countParents(treeItem.getParent());
    }

    public void setOnAddBigtableInstance(EventHandler<ActionEvent> eventHandler) {
        addInstanceButton.setOnAction(eventHandler);
    }

    public void addBigtableInstance(BigtableInstance instance, ChangeListener<Boolean> expandedListener) {

        TreeItem<String> projectTreeItem = treeView.getRoot()
                .getChildren()
                .stream().filter(p -> p.getValue().equals(instance.getProjectId()))
                .findFirst()
                .orElse(null);

        if (projectTreeItem == null) {
            projectTreeItem = new TreeItem<>(instance.getProjectId());
        }

        TreeItem<String> instanceTreeItem = new TreeItem<>(instance.getInstanceId());
        projectTreeItem.getChildren().add(instanceTreeItem);
        treeView.getRoot().getChildren().add(projectTreeItem);
        treeView.setVisible(true);
    }

    public ReadOnlyObjectProperty<BigtableTable> selectedTableProperty() {
        return selectedTableProperty;
    }

    public void addBigtableTables(List<BigtableTable> tables) {

        for (BigtableTable table : tables) {
            treeView.getRoot()
                    .getChildren()
                    .stream()
                    .filter(item -> item.getValue().equals(table.getProjectId()))
                    .flatMap(item -> item.getChildren().stream())
                    .filter(item -> item.getValue().equals(table.getInstanceId()))
                    .findFirst()
                    .ifPresent(instance -> {
                        if (instance.getChildren().stream().noneMatch(c -> c.getValue().equals(table.getTableId()))) {
                            instance.getChildren().add(new TreeItem<>(table.getTableId()));
                        }
            });

        }
    }

    private MenuItem getMenuItem(BigtableInstance instance) {
        MenuItem menuItem = new MenuItem(instance.getInstanceId());
        menuItem.setUserData(instance);
        return menuItem;
    }

    public ReadOnlyObjectProperty<BigtableInstance> selectedInstanceProperty() {
        return selectedInstanceProperty;
    }

    public ReadOnlyStringProperty selectedProjectProperty() {
        return selectedProjectProperty;
    }
}
