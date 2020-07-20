package com.erikmafo.btviewer.model;
import com.google.bigtable.admin.v2.Table;
import com.google.bigtable.admin.v2.TableName;

import java.util.Objects;

public class BigtableTable {

    private final String projectId;
    private final String instanceId;
    private final String tableId;

    public BigtableTable(String name) {
        var tab = TableName.parse(name);
        projectId = tab.getProject();
        instanceId = tab.getInstance();
        tableId = tab.getTable();
    }

    public BigtableTable(String projectId, String instanceId, String tableId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
        this.tableId = tableId;
    }

    public BigtableTable(BigtableInstance instance, String tableId) {
        this.projectId = instance.getProjectId();
        this.instanceId = instance.getInstanceId();
        this.tableId = tableId;
    }

    public String getName() {
        return String.format("projects/%s/instances/%s/tables/%s", projectId, instanceId, tableId);
    }

    public String getSimpleName() { return tableId; }

    public String getProjectId() {
        return projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getTableId() {
        return tableId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigtableTable that = (BigtableTable) o;
        return Objects.equals(projectId, that.projectId) &&
                Objects.equals(instanceId, that.instanceId) &&
                Objects.equals(tableId, that.tableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, instanceId, tableId);
    }
}
