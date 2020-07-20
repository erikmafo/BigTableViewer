package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableSettings;

import java.io.IOException;
import java.nio.file.Path;
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
