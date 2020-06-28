package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.services.internal.BigtableSettingsProvider;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ListTablesService extends Service<List<BigtableTable>> {

    private final BigtableSettingsProvider settingsProvider;

    private BigtableTableAdminClient client;
    private BigtableInstance instance;

    @Inject
    public ListTablesService(BigtableSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public void setInstance(BigtableInstance instance) {
        this.instance = instance;
    }

    @Override
    protected Task<List<BigtableTable>> createTask() {
        return new Task<>() {
            @Override
            protected List<BigtableTable> call() throws Exception {
                return getOrCreateNewClient()
                        .listTables()
                        .stream()
                        .map(ListTablesService.this::toBigtableTable)
                        .collect(Collectors.toList());
            }
        };
    }

    private BigtableTable toBigtableTable(String tableId) {
        return new BigtableTable(instance.getProjectId(), instance.getInstanceId(), tableId);
    }

    private BigtableTableAdminClient getOrCreateNewClient() throws IOException {
        if (instance == null) {
            throw new IllegalStateException("Cannot list tables when bigtable instance is not specified");
        }

        if (instance.equals(getClientInstance())) {
            return client;
        }

        closeClient();
        client = BigtableTableAdminClient.create(settingsProvider.getTableAdminSettings(instance));
        return client;
    }

    private void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    private BigtableInstance getClientInstance() {
        if (client == null) {
            return null;
        }

        return new BigtableInstance(client.getProjectId(), client.getInstanceId());
    }
}
