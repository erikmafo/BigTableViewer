package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.Query;

public class BigtableReadRequest {

    private final BigtableInstance instance;
    private final Query sqlQuery;

    BigtableReadRequest(BigtableInstance instance, Query sqlQuery) {
        this.instance = instance;
        this.sqlQuery = sqlQuery;
    }

    public BigtableInstance getInstance() {
        return instance;
    }

    public Query getSqlQuery() {
        return sqlQuery;
    }

    public BigtableTable getTable() {
        return new BigtableTable(instance.getProjectId(), instance.getInstanceId(), sqlQuery.getTableName());
    }
}
