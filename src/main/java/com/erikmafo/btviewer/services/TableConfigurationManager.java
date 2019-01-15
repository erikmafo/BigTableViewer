package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;
import com.google.gson.Gson;

import java.util.prefs.Preferences;

public class TableConfigurationManager {

    private static final String ROOT_NODE = "table-configs";

    private static Gson gson = new Gson();

    protected Preferences getPreferences() {
        return Preferences.userRoot().node(ROOT_NODE);
    }


    public BigtableTableConfiguration getTableConfiguration(BigtableTable table) {
        String json = getPreferences().get(table.getName(), null);

        if (json == null) {
            return null;
        }

        return gson.fromJson(json, BigtableTableConfiguration.class);
    }

    public void saveTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) {
        getPreferences().put(table.getName(), gson.toJson(configuration));
    }

}
