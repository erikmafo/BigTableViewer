package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;

import java.io.IOException;
import java.nio.file.Path;

public interface TableConfigManager {
    BigtableTableConfiguration getTableConfiguration(BigtableTable table) throws IOException;

    void saveTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) throws IOException;
}
