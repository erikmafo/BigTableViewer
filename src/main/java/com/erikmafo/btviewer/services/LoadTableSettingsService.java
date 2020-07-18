package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableSettings;
import com.erikmafo.btviewer.services.internal.AppDataStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;

public class LoadTableSettingsService extends Service<BigtableTableSettings> {

    private final AppDataStorage appDataStorage;
    private BigtableTable table;

    @Inject
    public LoadTableSettingsService(AppDataStorage appDataStorage) {
        this.appDataStorage = appDataStorage;
    }

    public void setTable(BigtableTable table) {
        this.table = table;
    }

    @Override
    protected Task<BigtableTableSettings> createTask() {
        return new Task<>() {
            @Override
            protected BigtableTableSettings call() throws Exception {
                var tableConfig = appDataStorage.getTableSettings(table);
                if (tableConfig == null) {
                    tableConfig = new BigtableTableSettings();
                }
                return tableConfig;
            }
        };
    }
}
