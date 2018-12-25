package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableCell;
import com.erikmafo.btviewer.model.BigtableRow;
import com.erikmafo.btviewer.model.CellDefinition;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.cloud.bigtable.grpc.scanner.FlatRow;
import com.google.cloud.bigtable.grpc.scanner.ResultScanner;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class BigtableResultScanner implements Closeable {

    private final BigtableSession session;
    private final ResultScanner<FlatRow> resultScanner;
    private final List<CellDefinition> cellDefinitions;
    private boolean isEndOfStream;

    public BigtableResultScanner(BigtableSession session, ResultScanner<FlatRow> resultScanner, List<CellDefinition> cellDefinitions) {
        this.session = session;
        this.resultScanner = resultScanner;
        this.cellDefinitions = cellDefinitions;
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

    public int available() {
        return resultScanner.available();
    }

    public BigtableRow next() throws IOException {

        if (isEndOfStream)
        {
            return null;
        }

        BigtableRow row = toBigtableRow(resultScanner.next());
        if (row == null)
        {
            isEndOfStream = true;
        }
        return row;
    }

    private CellDefinition getBigtableCell(String family, String qualifier) {

        return cellDefinitions
                .stream()
                .filter(r -> family.equals(r.getFamily()) && qualifier.equals(r.getQualifier()))
                .findFirst()
                .orElse(new CellDefinition("UndefinedValueType", family, qualifier));
    }

    private BigtableRow toBigtableRow(FlatRow flatRow) {

        if (flatRow == null) {
            return null;
        }

        List<BigtableCell> cells = new LinkedList<>();

        for (FlatRow.Cell cell : flatRow.getCells()) {
            String family = cell.getFamily();
            String qualifier = cell.getQualifier().toStringUtf8();

           /* CellDefinition cellDefinition = getBigtableCell(family, qualifier);

            Object value;

            if ("Float".equals(cellDefinition.getValueType())) {
                value = cell.getValue().asReadOnlyByteBuffer().getFloat();
            } else if ("Integer".equals(cellDefinition.getValueType())) {
                value = cell.getValue().asReadOnlyByteBuffer().getInt();
            } else if ("String".equals(cellDefinition.getValueType())) {
                value = cell.getValue().toStringUtf8();
            } else {
                value = cell.getValue().toStringUtf8();
            }*/

            cells.add(new BigtableCell(family, qualifier, cell.getValue()));
        }

        return new BigtableRow(flatRow.getRowKey().toStringUtf8(), cells);
    }
}
