package com.erikmafo.btviewer.services.inmemory;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableReadRequest;
import com.erikmafo.btviewer.model.BigtableRow;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.services.BigtableClient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBigtableClient implements BigtableClient {

    private final Map<BigtableTable, SortedMap<String, BigtableRow>> tables = new HashMap<>();

    @Override
    public List<BigtableRow> readRows(BigtableReadRequest readRequest) throws IOException {

        var table = tables.get(readRequest.getBigtableTable());

        if (table == null)
        {
            return new ArrayList<>();
        }

        return table
                .subMap(readRequest.getScan().getFrom(), readRequest.getScan().getTo())
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> listTables(BigtableInstance bigtableInstance, Path credentialsPath) throws IOException {
        return tables.keySet().stream().map(BigtableTable::getName).collect(Collectors.toList());
    }

    @Override
    public List<BigtableInstance> listInstances(String projectId, String credentialsPath) throws IOException {
        return null;
    }

    public void addRow(BigtableTable table, BigtableRow bigtableRow)
    {
        tables.putIfAbsent(table, new TreeMap<>());
        var rows = tables.get(table);
        rows.put(bigtableRow.getRowKey(), bigtableRow);
    }
}
