package com.erikmafo.btviewer.services.table;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableSettings;
import com.erikmafo.btviewer.services.internal.AppDataStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;

public class SaveTableSettingsService extends Service<Void> {

    private final AppDataStorage appDataStorage;

    private BigtableTable table;
    private BigtableTableSettings tableConfiguration;

    @Inject
    public SaveTableSettingsService(AppDataStorage appDataStorage) {
        this.appDataStorage = appDataStorage;
    }

    public void setTableConfiguration(BigtableTable table, BigtableTableSettings tableConfiguration) {
        this.table = table;
        this.tableConfiguration = tableConfiguration;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                appDataStorage.saveTableConfiguration(table, tableConfiguration);
                return null;
            }
        };
    }
}
