package com.erikmafo.ltviewer.ui.projectexplorer;

import com.erikmafo.ltviewer.model.BigtableInstance;
import com.erikmafo.ltviewer.model.BigtableTable;
import com.erikmafo.ltviewer.services.instance.SaveInstanceService;
import com.erikmafo.ltviewer.services.project.RemoveProjectService;
import com.erikmafo.ltviewer.util.AlertUtil;
import com.google.inject.Provider;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class ProjectExplorerController {

    @FXML
    private Button addInstanceButton;

    @FXML
    private TreeView<TreeItemData> treeView;

    private final Provider<RootTreeItem> rootTreeItemProvider;

    @NotNull
    private final SimpleObjectProperty<BigtableTable> selectedTableProperty;
    @NotNull
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

    @FXML
    public void initialize() {
        treeView.setRoot(rootTreeItemProvider.get());

        treeView.setCellFactory(tableInfoTreeView -> new TreeCell<>() {
            @Override
            protected void updateItem(@Nullable TreeItemData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setContextMenu(createContextMenu(item));
                    setText(item.getDisplayName());
                    if (item.isInstance()) {
                        var progressIndicator = new ProgressIndicator();
                        progressIndicator.setStyle(" -fx-progress-color: grey;");
                        progressIndicator.visibleProperty().bind(item.loadingProperty());
                        progressIndicator.setPrefSize(15, 15);
                        setGraphic(progressIndicator);
                        setGraphicTextGap(5);
                        setContentDisplay(ContentDisplay.RIGHT);
                    }
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

    @NotNull
    public ReadOnlyObjectProperty<BigtableInstance> selectedInstanceProperty() {
        return selectedInstanceProperty;
    }

    @NotNull
    public ReadOnlyObjectProperty<BigtableTable> selectedTableProperty() {
        return selectedTableProperty;
    }

    @Nullable
    public ContextMenu createContextMenu(@NotNull TreeItemData item){
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
        } else if (item.isInstance()) {
            var refreshTables = new MenuItem("Refresh tables");
            refreshTables.setOnAction(e -> ((InstanceTreeItem)item.getTreeItem()).loadChildren());
            menu = new ContextMenu(refreshTables);
        }

        return menu;
    }

    private void handleAddInstanceAction(ActionEvent ignore) {
        AddInstanceDialog.displayAndAwaitResult().whenComplete(this::handleAddInstanceResult);
    }

    private void handleAddInstanceResult(@Nullable BigtableInstance instance, Throwable throwable) {
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
