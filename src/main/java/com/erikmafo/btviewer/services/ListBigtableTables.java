package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListBigtableTables extends Service<List<BigtableTable>> {

    private final BigtableClient client;
    private final BigtableInstance instance;
    private final Path credentialsPath;

    public ListBigtableTables(BigtableClient client, BigtableInstance instance, Path credentialsPath) {
        this.client = client;
        this.instance = instance;
        this.credentialsPath = credentialsPath;
    }

    @Override
    protected Task<List<BigtableTable>> createTask() {
        return new Task<List<BigtableTable>>() {
            @Override
            protected List<BigtableTable> call() throws Exception {
                return client.listTables(
                        instance,
                        credentialsPath)
                        .stream()
                        .map(ListBigtableTables.this::getBigtableTable)
                        .collect(Collectors.toList());
            }
        };
    }

    private BigtableTable getBigtableTable(String tableName) {
        return new BigtableTable(tableName);
    }
}
