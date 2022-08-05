package com.erikmafo.little.table.viewer.services.table;

import com.erikmafo.little.table.viewer.model.BigtableTable;
import com.erikmafo.little.table.viewer.model.BigtableTableSettings;
import com.erikmafo.little.table.viewer.services.internal.AppDataStorage;
import com.erikmafo.little.table.viewer.util.AlertUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;

public class LoadTableSettingsService extends Service<BigtableTableSettings> {

    private final AppDataStorage appDataStorage;
    private BigtableTable table;

    @Inject
    public LoadTableSettingsService(AppDataStorage appDataStorage) {
        this.appDataStorage = appDataStorage;
        setOnFailed(event -> AlertUtil.displayError("Unable to load table setting", getException()));
    }

    public void setTable(BigtableTable table) {
        this.table = table;
    }

    @Override
    protected Task<BigtableTableSettings> createTask() {
        var table = this.table;
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
