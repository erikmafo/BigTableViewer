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

    private static final String ROWKEY = "rowkey";

    @FXML
    private Button configureRowValueTypesButton;

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
        //configureRowValueTypesButton.setVisible(false); // TODO: enable this feature
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

    public int getMaxRows() {
        ScrollBar bar = getVerticalScrollbar();
        return (int)bar.getMax();
    }

    public void setOnConfigureRowValuesTypes(EventHandler<ActionEvent> eventHandler) {
        configureRowValueTypesButton.setOnAction(eventHandler);
    }

    public void setOnScrollEvent(EventHandler<ScrollEvent> eventHandler) {
        getVerticalScrollbar().setOnScroll(eventHandler);
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

    public void clear() {
        tableView.getColumns().removeIf(t -> !t.getText().equals(ROWKEY));
        setBigTableRows(FXCollections.observableArrayList());
    }

    public void add(BigtableRow row) {
        tableView.getItems().add(row);
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
            return new ReadOnlyObjectWrapper<>(bigtableRow.getCellValue(family, qualifier, valueConverter));
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

    public void setValueConverter(BigtableValueConverter valueConverter) {
        this.valueConverter = valueConverter;
        var rows = this.tableView.getItems();
        clear();
        for (var row : rows) {
           add(row);
        }
    }
}
