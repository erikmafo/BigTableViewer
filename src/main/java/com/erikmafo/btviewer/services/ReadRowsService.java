package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.internal.AppDataStorage;
import com.erikmafo.btviewer.services.internal.BigtableSettingsProvider;
import com.erikmafo.btviewer.sql.QueryConverter;
import com.erikmafo.btviewer.sql.SqlQuery;
import com.erikmafo.btviewer.util.AlertUtil;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadRowsService extends Service<List<BigtableRow>> {

    private final BigtableSettingsProvider settingsProvider;
    private final AppDataStorage storage;

    private BigtableDataClient client;
    private BigtableDataSettings settings;

    private BigtableInstance instance;
    private SqlQuery query;

    @Inject
    public ReadRowsService(BigtableSettingsProvider settingsProvider, AppDataStorage storage) {
        this.settingsProvider = settingsProvider;
        this.storage = storage;
    }

    public void setInstance(BigtableInstance instance) { this.instance = instance; }

    public void setQuery(SqlQuery query) { this.query = query; }

    @Override
    protected Task<List<BigtableRow>> createTask() {
        var instance = this.instance;
        var sqlQuery = this.query;
        var tableId = sqlQuery.getTableName();

        return new Task<>() {
            @Override
            protected List<BigtableRow> call() throws Exception {
                var tableSettings = storage.getTableSettings(new BigtableTable(instance, tableId));
                var queryConverter = new QueryConverter(new ByteStringConverterImpl(tableSettings));
                var btQuery = queryConverter.toBigtableQuery(sqlQuery);
                var rowIterator = getOrCreateNewClient(instance).readRows(btQuery).iterator();
                var bigtableRows = new LinkedList<BigtableRow>();
                while (rowIterator.hasNext()) {
                    bigtableRows.add(toBigtableRow(rowIterator.next()));
                    updateProgress(bigtableRows.size(), sqlQuery.getLimit());
                    if (isCancelled()) {
                        break;
                    }
                }
                return bigtableRows;
            }
        };
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
                cell.getValue(),
                cell.getTimestamp());
    }

    private BigtableDataClient getOrCreateNewClient(BigtableInstance instance) throws IOException {
        if (instance == null) {
            throw new IllegalStateException("Cannot list tables when read request is not specified");
        }

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


