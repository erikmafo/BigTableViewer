package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableCell;
import com.erikmafo.btviewer.model.BigtableRow;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.scanner.FlatRow;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.scanner.ResultScanner;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class BigtableResultScanner implements Closeable {

    private final BigtableSession session;
    private final ResultScanner<FlatRow> resultScanner;
    private boolean isEndOfStream;

    public BigtableResultScanner(BigtableSession session, ResultScanner<FlatRow> resultScanner) {
        this.session = session;
        this.resultScanner = resultScanner;
    }

    @Override
    public void close() throws IOException {
        session.close();
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

    private BigtableRow toBigtableRow(FlatRow flatRow) {

        if (flatRow == null) {
            return null;
        }

        List<BigtableCell> cells = new LinkedList<>();

        for (FlatRow.Cell cell : flatRow.getCells()) {
            String family = cell.getFamily();
            String qualifier = cell.getQualifier().toStringUtf8();
            cells.add(new BigtableCell(family, qualifier, cell.getValue()));
        }

        return new BigtableRow(flatRow.getRowKey().toStringUtf8(), cells);
    }
}
