package com.erikmafo.btviewer.model;
import com.google.cloud.bigtable.grpc.BigtableTableName;
import java.util.Collections;
import java.util.List;

public class BigtableTable {

    private final String projectId;
    private final String instanceId;
    private final String tableId;

    private final List<CellDefinition> cellDefinitions;

    public BigtableTable(String name) {
        BigtableTableName tableName = new BigtableTableName(name);
        projectId = tableName.getProjectId();
        instanceId = tableName.getInstanceId();
        tableId = tableName.getTableId();
        cellDefinitions = Collections.emptyList();
    }

    public BigtableTable(String projectId, String instanceId, String tableId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
        this.tableId = tableId;
        cellDefinitions = Collections.emptyList();
    }

    public String getName() {
        return String.format("projects/%s/instances/%s/tables/%s", projectId, instanceId, tableId);
    }

    public String getSimpleName() { return tableId; }

    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getTableId() {
        return tableId;
    }
}
