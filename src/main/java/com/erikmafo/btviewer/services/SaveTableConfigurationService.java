package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;
import com.erikmafo.btviewer.services.internal.TableConfigManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;

public class SaveTableConfigurationService extends Service<Void> {

    private final TableConfigManager tableConfigManager;

    private BigtableTable table;
    private BigtableTableConfiguration tableConfiguration;

    @Inject
    public SaveTableConfigurationService(TableConfigManager tableConfigManager) {
        this.tableConfigManager = tableConfigManager;
    }

    public void setTableConfiguration(BigtableTable table, BigtableTableConfiguration tableConfiguration) {
        this.table = table;
        this.tableConfiguration = tableConfiguration;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                tableConfigManager.saveTableConfiguration(table, tableConfiguration);
                return null;
            }
        };
    }
}
