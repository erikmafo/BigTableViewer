package com.erikmafo.btviewer.services.query;

import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.internal.AppDataStorage;
import com.erikmafo.btviewer.services.internal.BigtableSettingsProvider;
import com.erikmafo.btviewer.sql.QueryConverter;
import com.erikmafo.btviewer.sql.SqlQuery;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * A service that executes queries against Bigtable.
 */
public class BigtableQueryService extends Service<List<QueryResultRow>> {

    private final BigtableSettingsProvider settingsProvider;
    private final AppDataStorage storage;

    private BigtableDataClient client;
    private BigtableDataSettings settings;

    private BigtableInstance instance;
    private SqlQuery query;

    @Inject
    public BigtableQueryService(BigtableSettingsProvider settingsProvider, AppDataStorage storage) {
        this.settingsProvider = settingsProvider;
        this.storage = storage;
    }

    public void setInstance(BigtableInstance instance) { this.instance = instance; }

    public void setQuery(SqlQuery query) { this.query = query; }

    @Override
    protected Task<List<QueryResultRow>> createTask() {
        var instance = this.instance;
        var sqlQuery = this.query;
        var tableId = sqlQuery.getTableName();

        return new Task<>() {

            @Override
            protected List<QueryResultRow> call() throws Exception {
                var tableSettings = storage.getTableSettings(new BigtableTable(instance, tableId));
                var queryConverter = new QueryConverter(sqlQuery, new ByteStringConverterImpl(tableSettings));
                var valueConverter = new BigtableValueConverter(tableSettings.getCellDefinitions());
                var btQuery = queryConverter.toBigtableQuery();
                var rows = getOrCreateNewClient(instance).readRows(btQuery);
                var rowStream = StreamSupport.stream(rows.spliterator(), true);
                return new QueryResultConverter(sqlQuery, valueConverter).toQueryResultRows(rowStream);
            }
        };
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


