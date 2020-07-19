package com.erikmafo.btviewer.projectexplorer;
import com.erikmafo.btviewer.components.AddInstanceDialog;
import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.services.RemoveProjectService;
import com.erikmafo.btviewer.services.SaveInstanceService;
import com.erikmafo.btviewer.util.AlertUtil;
import com.google.inject.Provider;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.inject.Inject;

public class ProjectExplorerController {

    @FXML
    private Button addInstanceButton;

    @FXML
    private TreeView<TreeItemData> treeView;

    private final Provider<RootTreeItem> rootTreeItemProvider;

    private final SimpleObjectProperty<BigtableTable> selectedTableProperty;
    private final SimpleObjectProperty<BigtableInstance> selectedInstanceProperty;

    private final SaveInstanceService saveInstanceService;
    private final RemoveProjectService removeProjectService;

    @Inject
    public ProjectExplorerController(
            Provider<RootTreeItem> rootTreeItemProvider,
            SaveInstanceService saveInstanceService,
            RemoveProjectService removeProjectService) {
        this.rootTreeItemProvider = rootTreeItemProvider;
        this.saveInstanceService = saveInstanceService;
        this.removeProjectService = removeProjectService;
        this.selectedTableProperty = new SimpleObjectProperty<>();
        this.selectedInstanceProperty = new SimpleObjectProperty<>();
    }

    public void initialize() {
        treeView.setRoot(rootTreeItemProvider.get());
        treeView.setCellFactory(tableInfoTreeView -> new TreeCell<>() {
            @Override
            protected void updateItem(TreeItemData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setContextMenu(createContextMenu(item));
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
        addInstanceButton.setOnAction(this::handleAddInstanceAction);
    }

    public ReadOnlyObjectProperty<BigtableInstance> selectedInstanceProperty() {
        return selectedInstanceProperty;
    }

    public ReadOnlyObjectProperty<BigtableTable> selectedTableProperty() {
        return selectedTableProperty;
    }

    public ContextMenu createContextMenu(TreeItemData item){
        ContextMenu menu = null;
        if (item.isProject()) {
            var addInstance = new MenuItem("Add instance");
            addInstance.setOnAction(actionEvent ->
                    AddInstanceDialog
                            .displayAndAwaitResult(item.getProjectId())
                            .whenComplete(this::handleAddInstanceResult));
            var removeProject = new MenuItem("Remove");
            removeProject.setOnAction(actionEvent -> {
                removeProjectService.setProjectId(item.getProjectId());
                removeProjectService.setOnSucceeded(event -> ((RootTreeItem)treeView.getRoot()).removeProject((item.getProjectId())));
                removeProjectService.setOnFailed(event -> AlertUtil.displayError("Unable to remove project", event));
                removeProjectService.restart();
            });

            menu = new ContextMenu(addInstance, removeProject);
        }

        return menu;
    }

    private void handleAddInstanceAction(ActionEvent ignore) {
        AddInstanceDialog.displayAndAwaitResult().whenComplete(this::handleAddInstanceResult);
    }

    private void handleAddInstanceResult(BigtableInstance instance, Throwable throwable) {
        if (instance == null) {
            return;
        }
        saveInstanceService.setInstance(instance);
        saveInstanceService.setOnFailed(event -> AlertUtil.displayError("Unable to save instance", event));
        saveInstanceService.setOnSucceeded(event -> reloadOrAddProject(instance.getProjectId()));
        saveInstanceService.restart();
    }

    private void reloadOrAddProject(String projectId) {
        ((RootTreeItem)treeView.getRoot()).reloadOrAddProject(projectId);
    }
}
