package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;
import com.erikmafo.btviewer.services.internal.TableConfigManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.LinkedList;

public class LoadTableConfigurationService extends Service<BigtableTableConfiguration> {

    private final TableConfigManager tableConfigManager;
    private BigtableTable table;

    @Inject
    public LoadTableConfigurationService(TableConfigManager tableConfigManager) {
        this.tableConfigManager = tableConfigManager;
    }

    public void setTable(BigtableTable table) {
        this.table = table;
    }

    @Override
    protected Task<BigtableTableConfiguration> createTask() {
        return new Task<>() {
            @Override
            protected BigtableTableConfiguration call() throws Exception {
                var tableConfig = tableConfigManager.getTableConfiguration(table);
                if (tableConfig == null) {
                    tableConfig = new BigtableTableConfiguration(table);
                }
                return tableConfig;
            }
        };
    }
}
