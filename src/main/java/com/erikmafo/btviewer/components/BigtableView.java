package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.model.BigtableCell;
import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableRow;
import com.erikmafo.btviewer.services.BigtableResultScanner;
import com.google.cloud.bigtable.grpc.BigtableTableName;
import com.sun.javafx.collections.ObservableSetWrapper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 12.12.17.
 */
public class BigtableView extends GridPane {

    public static final String ROWKEY = "rowkey";

    @FXML
    private TableView<BigtableRow> tableView;

    private BigtableResultScanner scanner;

    public BigtableView() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bigtable_view.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load fxml", e);
        }

        tableView.getColumns().add(createRowKeyColumn());
        tableView.setOnScroll(event -> {
            ScrollBar bar = getVerticalScrollbar();
            Platform.runLater(() -> addRows((int)bar.getMax()));
        });
    }

    private ScrollBar getVerticalScrollbar() {
        ScrollBar result = null;
        for (Node n : tableView.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    private TableColumn<BigtableRow, ?> createRowKeyColumn() {
        TableColumn<BigtableRow, Object> tableColumn = new TableColumn<>(ROWKEY);
        tableColumn.setCellValueFactory(param -> {
            BigtableRow bigtableRow = param.getValue();
            return new ReadOnlyObjectWrapper<>(bigtableRow.getRowKey());
        });
        return tableColumn;
    }

    public List<BigtableColumn> getColumns() {
        return tableView.getColumns()
                .stream()
                .filter(c -> !c.getText().equals(ROWKEY))
                .flatMap(f -> f.getColumns()
                        .stream()
                        .map(q -> new BigtableColumn(f.getText(), q.getText())))
                .collect(Collectors.toList());
    }

    public void setBigtableScanner(BigtableResultScanner scanner)
    {
        if (this.scanner != null)
        {
            BigtableResultScanner prevScanner = this.scanner;
            Platform.runLater(() ->
            {
                try {
                    prevScanner.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        this.scanner = scanner;
        tableView.getColumns().removeIf(t -> !t.getText().equals(ROWKEY));
        setBigTableRows(FXCollections.observableArrayList());
        Platform.runLater(() -> addRows(20));
    }

    private void addColumn(String family, String qualifier) {

        TableColumn<BigtableRow, ?> familyColumn = tableView.getColumns()
                .stream()
                .filter(c -> c.getText().equals(family))
                .findFirst()
                .orElse(null);

        TableColumn<BigtableRow, Object> qualifierColumn = new TableColumn<>(qualifier);
        qualifierColumn.setCellValueFactory(param -> {
            BigtableRow bigtableRow = param.getValue();
            return new ReadOnlyObjectWrapper<>(bigtableRow.getCellValue(family, qualifier));
        });


        if (familyColumn == null) {
            familyColumn = new TableColumn<>(family);
            familyColumn.getColumns().add(qualifierColumn);
            tableView.getColumns().add(familyColumn);
        } else if (familyColumn.getColumns().stream().noneMatch(c -> c.getText().equals(qualifier))) {
            familyColumn.getColumns().add(qualifierColumn);
        }
    }

    private void setBigTableRows(ObservableList<BigtableRow> bigtableRows) {
        tableView.setItems(bigtableRows);
        bigtableRows.addListener((ListChangeListener<BigtableRow>) change -> {
            while (change.next()) {
                change.getAddedSubList()
                        .stream()
                        .flatMap(r -> r.getCells().stream())
                        .forEach(cell -> addColumn(cell.getFamily(), cell.getQualifier()));
            }
        });
    }

    private void addRows(int maxRows) {
        int count = 0;

        BigtableRow row;
        do {
            try {
                row = scanner.next();
                if (row == null) {
                    break;
                }
                tableView.getItems().add(row);
                count++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (count < maxRows);
    }
}
