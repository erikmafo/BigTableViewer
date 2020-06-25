package com.erikmafo.btviewer.model;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.BigtableTableName;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BigtableTable {

    private final String projectId;
    private final String instanceId;
    private final String tableId;

    public BigtableTable(String name) {
        BigtableTableName tableName = new BigtableTableName(name);
        projectId = tableName.getProjectId();
        instanceId = tableName.getInstanceId();
        tableId = tableName.getTableId();
    }

    public BigtableTable(String projectId, String instanceId, String tableId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
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
