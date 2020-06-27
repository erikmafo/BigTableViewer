package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableReadRequest;
import com.erikmafo.btviewer.model.BigtableRow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface BigtableClient {
    List<BigtableRow> readRows(BigtableReadRequest readRequest) throws IOException;

    List<String> listTables(BigtableInstance instance, Path credentialsPath) throws IOException;

    List<BigtableInstance> listInstances(String projectId, String credentialsPath) throws IOException;
}
