package com.erikmafo.btviewer.ui.projectexplorer;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;

import java.util.Objects;

public class TreeItemData {

    private String projectId;
    private String instanceId;
    private String tableId;

    private final BooleanProperty loading = new SimpleBooleanProperty();
    private final ObjectProperty<TreeItem<TreeItemData>> treeItem = new SimpleObjectProperty<>();

    public TreeItemData() {
    }

    public TreeItemData(String projectId) {
        this(projectId, null, null);
    }

    public TreeItemData(String projectId, String instanceId) {
        this(projectId, instanceId, null);
    }

    public TreeItemData(String projectId, String instanceId, String tableId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
        this.tableId = tableId;
    }

    public TreeItemData(BigtableInstance instance) {
        this(instance.getProjectId(), instance.getInstanceId());
    }

    public TreeItemData(BigtableTable table) {
        this(table.getProjectId(), table.getInstanceId(), table.getTableId());
    }

    public boolean isRoot() { return projectId == null; }

    public boolean isProject() {
        return !isRoot() && instanceId == null && tableId == null;
    }

    public boolean isInstance() {
        return instanceId != null && tableId == null;
    }

    public boolean isTable() {
        return instanceId != null && tableId != null;
    }

    public String getDisplayName() {
        String displayName;
        if (isRoot()) {
            displayName = "Projects";
        } else if (isProject()) {
            displayName = projectId;
        } else if (isInstance()) {
            displayName = instanceId;
        } else {
            displayName = tableId;
        }

        return displayName;
    }

    public BigtableTable toTable() {
        return new BigtableTable(projectId, instanceId, tableId);
    }

    public BigtableInstance toInstance() {
        return new BigtableInstance(projectId, instanceId);
    }

    public String getProjectId() {
        return projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeItemData treeItemData = (TreeItemData) o;

        if (!projectId.equals(treeItemData.projectId)) return false;
        if (!Objects.equals(instanceId, treeItemData.instanceId)) return false;
        return Objects.equals(tableId, treeItemData.tableId);
    }

    @Override
    public int hashCode() {
        int result = projectId.hashCode();
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
        result = 31 * result + (tableId != null ? tableId.hashCode() : 0);
        return result;
    }

    public boolean isLoading() {
        return loading.get();
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading.set(loading);
    }

    public TreeItem<TreeItemData> getTreeItem() {
        return treeItem.get();
    }

    public ObjectProperty<TreeItem<TreeItemData>> treeItemProperty() {
        return treeItem;
    }

    public void setTreeItem(TreeItem<TreeItemData> treeItem) {
        this.treeItem.set(treeItem);
    }
}
