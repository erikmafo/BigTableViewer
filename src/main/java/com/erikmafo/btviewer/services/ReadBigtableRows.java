package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.util.ArrayList;
import java.util.List;

public class ReadBigtableRows extends Service<List<BigtableRow>> {

    private final BigtableClient client;
    private final BigtableReadRequest readRequest;

    public ReadBigtableRows(BigtableClient client, BigtableReadRequest readRequest) {
        this.client = client;
        this.readRequest = readRequest;
    }

    @Override
    protected Task<List<BigtableRow>> createTask() {
        return new Task<>() {
            @Override
            protected List<BigtableRow> call() throws Exception {

                try (BigtableResultScanner scanner = client.execute(readRequest)) {
                    List<BigtableRow> result = new ArrayList<>();
                    int count = 0;
                    BigtableRow row;
                    do {
                        count++;
                        row = scanner.next();
                        if (row == null) {
                            break;
                        }
                        result.add(row);
                        updateProgress(count, readRequest.getScan().getMaxRows());

                    } while (count < readRequest.getScan().getMaxRows());
                    return result;
                }
            }
        };
    }
}


