package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TableConfigManagerImpl implements TableConfigManager {

    private static final String TABLES = "table-configs";

    private static Gson gson = new Gson();

    @Override
    public BigtableTableConfiguration getTableConfiguration(BigtableTable table) throws IOException {
        String json = null;
        var tableConfigFile = getConfigurationFile(table);
        if (Files.exists(tableConfigFile))
        {
            json = Files.readString(tableConfigFile);
        }

        if (json == null) {
            return null;
        }

        return gson.fromJson(json, BigtableTableConfiguration.class);
    }

    @Override
    public void saveTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) throws IOException {
        var configFile = getConfigurationFile(table);
        createDirectoriesIfNeeded(configFile);
        Files.writeString(getConfigurationFile(table), gson.toJson(configuration));
    }

    private void createDirectoriesIfNeeded(Path tableConfigFile) throws IOException {
        if (!Files.exists(tableConfigFile.getParent())) {
            Files.createDirectories(tableConfigFile.getParent());
        }
    }

    private Path getConfigurationFile(BigtableTable table) {
        return AppDataUtil
                .getStorageFolder()
                .resolve(TableConfigManagerImpl.TABLES)
                .resolve(table.getProjectId())
                .resolve(table.getInstanceId())
                .resolve(table.getTableId() + ".json");
    }

}
