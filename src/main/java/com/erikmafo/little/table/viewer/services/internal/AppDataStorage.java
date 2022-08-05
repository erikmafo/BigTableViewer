package com.erikmafo.little.table.viewer.services.internal;

import com.erikmafo.little.table.viewer.model.BigtableInstance;
import com.erikmafo.little.table.viewer.model.BigtableTable;
import com.erikmafo.little.table.viewer.model.BigtableTableSettings;

import java.io.IOException;
import java.util.List;

public interface AppDataStorage {

    List<String> getProjects() throws IOException;

    void removeProject(String projectId) throws IOException;

    List<BigtableInstance> getInstances(String projectId) throws IOException;

    void addInstance(BigtableInstance instance) throws IOException;

    void removeInstance(BigtableInstance instance) throws IOException;

    BigtableTableSettings getTableSettings(BigtableTable table) throws IOException;

    void saveTableConfiguration(BigtableTable table, BigtableTableSettings configuration) throws IOException;
}
