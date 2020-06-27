package com.erikmafo.btviewer.services.inmemory;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;
import com.erikmafo.btviewer.services.TableConfigManager;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTableConfigManager implements TableConfigManager {

    private final Map<BigtableTable, BigtableTableConfiguration> configs = new HashMap<>();

    @Override
    public BigtableTableConfiguration getTableConfiguration(BigtableTable table) {
        return configs.getOrDefault(table, null);
    }

    @Override
    public void saveTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) {
        configs.put(table, configuration);
    }
}
