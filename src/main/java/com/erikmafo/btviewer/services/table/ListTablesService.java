package com.erikmafo.btviewer.services.table;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.services.internal.BigtableSettingsProvider;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ListTablesService extends Service<List<BigtableTable>> {

    private final BigtableSettingsProvider settingsProvider;
    private final List<BigtableInstance> instances = new LinkedList<>();
    private BigtableInstance instance;

    @Inject
    public ListTablesService(BigtableSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public void setInstance(BigtableInstance instance) {
        this.instance = instance;
    }

    public void addInstances(@NotNull List<BigtableInstance> instances) {
        for(var instance : instances) {
            addInstance(instance);
        }
    }

    public void addInstance(BigtableInstance instance) {
        instances.add(instance);
    }

    @Override
    protected Task<List<BigtableTable>> createTask() {
        return new Task<>() {
            @Override
            protected List<BigtableTable> call() throws Exception {
                try (var client = createClient(instance)) {
                    return client
                            .listTables()
                            .stream()
                            .map(tableId -> toBigtableTable(instance, tableId))
                            .collect(Collectors.toList());
                }
            }
        };
    }

    @NotNull
    @Contract("_, _ -> new")
    private BigtableTable toBigtableTable(@NotNull BigtableInstance instance, String tableId) {
        return new BigtableTable(instance.getProjectId(), instance.getInstanceId(), tableId);
    }

    @NotNull
    @Contract("null -> fail")
    private BigtableTableAdminClient createClient(BigtableInstance instance) throws IOException {
        if (instance == null) {
            throw new IllegalStateException("Cannot list tables when bigtable instance is not specified");
        }
        return BigtableTableAdminClient.create(settingsProvider.getTableAdminSettings(instance));
    }
}
