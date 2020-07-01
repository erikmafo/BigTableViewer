package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.internal.BigtableSettingsProvider;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadRowsService extends Service<List<BigtableRow>> {

    private final BigtableSettingsProvider settingsProvider;

    private BigtableDataClient client;
    private BigtableDataSettings settings;
    private BigtableReadRequest readRequest;

    @Inject
    public ReadRowsService(BigtableSettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    @Override
    protected Task<List<BigtableRow>> createTask() {
        return new Task<>() {
            @Override
            protected List<BigtableRow> call() throws Exception {
                var rowIterator = getOrCreateNewClient()
                        .readRows(createQuery(readRequest))
                        .iterator();

                var bigtableRows = new ArrayList<BigtableRow>();
                while (rowIterator.hasNext()) {
                    bigtableRows.add(toBigtableRow(rowIterator.next()));
                    updateProgress(bigtableRows.size(), readRequest.getMaxRows());
                }
                return bigtableRows;
            }
        };
    }

    public void setReadRequest(BigtableReadRequest readRequest) {
        this.readRequest = readRequest;
    }

    private static Query createQuery(BigtableReadRequest request) {
        return Query
                .create(request.getTable().getTableId())
                .prefix(request.getPrefix())
                .range(request.getRange().getFrom(), request.getRange().getTo())
                .limit(request.getMaxRows());
    }

    private static BigtableRow toBigtableRow(Row row) {
        return new BigtableRow(row.getKey().toStringUtf8(), getBigtableCells(row));
    }

    private static List<BigtableCell> getBigtableCells(Row row) {
        return row
                .getCells()
                .stream()
                .map(ReadRowsService::toBigtableCell)
                .collect(Collectors.toList());
    }

    private static BigtableCell toBigtableCell(RowCell cell) {
        return new BigtableCell(
                cell.getFamily(),
                cell.getQualifier().toStringUtf8(),
                cell.getValue());
    }

    private BigtableDataClient getOrCreateNewClient() throws IOException {
        if (readRequest == null) {
            throw new IllegalStateException("Cannot list tables when read request is not specified");
        }

        var instance = new BigtableInstance(
                readRequest.getTable().getProjectId(),
                readRequest.getTable().getInstanceId());

        if (instance.equals(getClientInstance())) {
            return client;
        }

        closeClient();
        settings = settingsProvider.getDataSettings(instance);
        client = BigtableDataClient.create(settings);
        return client;
    }

    private BigtableInstance getClientInstance() {
        if (client == null) {
            return null;
        }

        return new BigtableInstance(settings.getProjectId(), settings.getInstanceId());
    }

    private void closeClient() {
        if (client != null) {
            client.close();
        }
    }
}


