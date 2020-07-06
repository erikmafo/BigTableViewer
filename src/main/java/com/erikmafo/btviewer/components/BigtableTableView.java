package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableRow;
import com.erikmafo.btviewer.model.BigtableValueConverter;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 12.12.17.
 */
public class BigtableTableView extends VBox {

    private static final String ROW_KEY = "key";

    @FXML
    private Button tableSettingsButton;

    @FXML
    private TableView<BigtableRow> tableView;

    private BigtableValueConverter valueConverter;

    public BigtableTableView() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bigtable_table_view.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load fxml", e);
        }

        tableView.getColumns().add(createRowKeyColumn());
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
        TableColumn<BigtableRow, Object> tableColumn = new TableColumn<>(ROW_KEY);
        tableColumn.setCellValueFactory(param -> {
            BigtableRow bigtableRow = param.getValue();
            return new ReadOnlyObjectWrapper<>(bigtableRow.getRowKey());
        });
        return tableColumn;
    }

    public int getMaxRows() {
        ScrollBar bar = getVerticalScrollbar();
        return (int)bar.getMax();
    }

    public void setOnTableSettingsChanged(EventHandler<ActionEvent> eventHandler) {
        tableSettingsButton.setOnAction(eventHandler);
    }

    public List<BigtableColumn> getColumns() {
        return tableView.getColumns()
                .stream()
                .filter(c -> !c.getText().equals(ROW_KEY))
                .flatMap(c -> c
                        .getColumns()
                        .stream()
                        .map(q -> new BigtableColumn(c.getText(), q.getText())))
                .collect(Collectors.toList());
    }

    public void clear() {
        tableView.getColumns().removeIf(t -> !t.getText().equals(ROW_KEY));
        setBigTableRows(FXCollections.observableArrayList());
    }

    public void add(BigtableRow row) {
        tableView.getItems().add(row);
    }

    private void addColumn(String family, String qualifier) {

        var familyColumn = getFamilyTableColumn(family);
        var qualifierColumn = getQualifierTableColumn(family, qualifier);

        if (familyColumn == null) {
            familyColumn = new TableColumn<>(family);
            familyColumn.getColumns().add(qualifierColumn);
            tableView.getColumns().add(familyColumn);
        } else if (familyColumn.getColumns().stream().noneMatch(c -> c.getText().equals(qualifier))) {
            familyColumn.getColumns().add(qualifierColumn);
        }
    }

    private TableColumn<BigtableRow, Object> getQualifierTableColumn(String family, String qualifier) {
        TableColumn<BigtableRow, Object> qualifierColumn = new TableColumn<>(qualifier);
        qualifierColumn.setCellValueFactory(param -> {
            var cell = param.getValue().getLatestCell(family, qualifier);
            return new ReadOnlyObjectWrapper<>(valueConverter.convert(cell));
        });
        return qualifierColumn;
    }

    private TableColumn<BigtableRow, ?> getFamilyTableColumn(String family) {
        return tableView.getColumns()
                    .stream()
                    .filter(c -> c.getText().equals(family))
                    .findFirst()
                    .orElse(null);
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

    public void setValueConverter(BigtableValueConverter valueConverter) {
        if (valueConverter.equals(this.valueConverter)) {
            return;
        }

        this.valueConverter = valueConverter;
        var rows = this.tableView.getItems();
        clear();
        for (var row : rows) {
           add(row);
        }
    }
}
