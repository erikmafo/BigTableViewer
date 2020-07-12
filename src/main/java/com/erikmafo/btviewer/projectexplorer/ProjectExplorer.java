package com.erikmafo.btviewer.projectexplorer;
import com.erikmafo.btviewer.events.InstanceTreeItemExpanded;
import com.erikmafo.btviewer.events.ProjectTreeItemExpanded;
import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import com.google.inject.Provider;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.inject.Inject;
import java.util.List;

public class ProjectExplorer {

    @FXML
    private Button addInstanceButton;

    @FXML
    private TreeView<TreeItemData> treeView;

    private final Provider<RootTreeItem> rootTreeItemProvider;

    private final SimpleObjectProperty<BigtableTable> selectedTableProperty;
    private final SimpleObjectProperty<BigtableInstance> selectedInstanceProperty;
    private EventHandler<ProjectTreeItemExpanded> projectItemExpandedHandler;
    private EventHandler<InstanceTreeItemExpanded> instanceItemExpandedHandler;

    @Inject
    public ProjectExplorer(Provider<RootTreeItem> rootTreeItemProvider) {
        this.rootTreeItemProvider = rootTreeItemProvider;
        selectedTableProperty = new SimpleObjectProperty<>();
        selectedInstanceProperty = new SimpleObjectProperty<>();
    }

    public void initialize() {
        var root = rootTreeItemProvider.get();
        treeView.setRoot(root);
        treeView.setCellFactory(tableInfoTreeView -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeItemData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        treeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, prevSelected, selectedItem) -> {
                    var selected = selectedItem.getValue();
                    if (selected.isTable()) {
                        selectedInstanceProperty.set(selected.toInstance());
                        selectedTableProperty.set(selected.toTable());
                    } else if (selected.isInstance()) {
                        selectedInstanceProperty.set(selected.toInstance());
                    }
        });
        treeView.setVisible(true);
    }

    public void setOnCreateNewBigtableInstance(EventHandler<ActionEvent> eventHandler) {
    }

    public void addBigtableInstances(List<BigtableInstance> bigtableInstances) {
    }

    public void addBigtableInstance(BigtableInstance instance) {
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
    }
}
