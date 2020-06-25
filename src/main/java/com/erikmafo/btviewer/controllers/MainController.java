package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.*;
import com.erikmafo.btviewer.events.ScanTableAction;
import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by erikmafo on 23.12.17.
 */
public class MainController {

    @FXML
    private RowSelectionView rowSelectionView;

    @FXML
    private BigtableTablesListView tablesListView;

    @FXML
    private Label tableNameLabel;

    @FXML
    private BigtableTableView bigtableTableView;

    private final CredentialsManager credentialsManager;
    private final BigtableInstanceManager bigtableInstanceManager;
    private final TableConfigManager tableConfigManager;
    private final BigtableClient bigtableClient;

    @Inject
    public MainController(CredentialsManager credentialsManager,
                          BigtableInstanceManager bigtableInstanceManager,
                          TableConfigManager tableConfigManager,
                          BigtableClient bigtableClient) {
        this.credentialsManager = credentialsManager;
        this.bigtableInstanceManager = bigtableInstanceManager;
        this.tableConfigManager = tableConfigManager;
        this.bigtableClient = bigtableClient;
    }

    public void initialize() {
        rowSelectionView.setVisible(false);
        bigtableTableView.setVisible(false);
        tableNameLabel.setVisible(false);
        bigtableTableView.setOnConfigureRowValuesTypes(this::onConfigureRowValueTypes);
        tablesListView.setOnCreateNewBigtableInstance(this::onAddNewBigtableInstance);
        tablesListView.selectedTableProperty().addListener(this::onBigtableTableSelected);
        tablesListView.setTreeItemExpandedEventHandler(event ->
                event.getBigtableInstances().forEach(MainController.this::listBigtableTables));
        rowSelectionView.setOnScanTable(this::onScanTableAction);
        loadBigtableInstances();
    }

    private void loadBigtableInstances() {
        try {
            tablesListView.addBigtableInstances(bigtableInstanceManager.getInstances());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: display error
        }
    }

    private void onAddNewBigtableInstance(ActionEvent event) {
        BigtableInstanceDialog.displayAndAwaitResult()
                .whenComplete((instance, throwable) -> {
                    saveInstance(instance);
                    tablesListView.addBigtableInstance(instance);
                    listBigtableTables(instance);
                });
    }

    private void saveInstance(BigtableInstance instance) {
        try {
            List<BigtableInstance> allInstances = bigtableInstanceManager.getInstances();
            allInstances.add(instance);
            bigtableInstanceManager.setInstances(allInstances);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: display error
        }
    }

    private void listBigtableTables(BigtableInstance instance) {
        ListBigtableTables listBigtableTables =
                new ListBigtableTables(bigtableClient, instance, credentialsManager.getCredentialsPath());
        listBigtableTables.setOnSucceeded(workerStateEvent ->
                tablesListView.addBigtableTables(listBigtableTables.getValue()));
        listBigtableTables.start();
    }

    private void onScanTableAction(ScanTableAction actionEvent) {
        bigtableTableView.clear();
        BigtableTable currentTable = tablesListView.selectedTableProperty().get();
        loadTableConfiguration(currentTable);
        BigtableReadRequest request = new BigtableReadRequestBuilder()
                .setCredentialsPath(credentialsManager.getCredentialsPath())
                .setTable(currentTable)
                .setRowRange(new BigtableRowRange(actionEvent.getFrom(), actionEvent.getTo()))
                .build();

        readBigtableRows(request);
    }

    private void loadTableConfiguration(BigtableTable currentTable) {
        try {
            var tableConfiguration = tableConfigManager.getTableConfiguration(currentTable);
            bigtableTableView.setValueConverter(new BigtableValueConverter(tableConfiguration.getCellDefinitions()));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: display error
        }
    }

    private void onConfigureRowValueTypes(ActionEvent event) {
        var table = tablesListView.selectedTableProperty().get();
        var currentTableConfig = getTableConfiguration(table);
        BigtableValueTypesDialog.displayAndAwaitResult(bigtableTableView.getColumns(), currentTableConfig)
                .whenComplete((configuration, throwable) -> {
                    bigtableTableView.setValueConverter(new BigtableValueConverter(configuration.getCellDefinitions()));
                    saveTableConfiguration(table, configuration);
                });
    }

    private BigtableTableConfiguration getTableConfiguration(BigtableTable table) {
        BigtableTableConfiguration currentTableConfig = null;
        try {
            currentTableConfig = tableConfigManager.getTableConfiguration(table);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: display error
        }
        return currentTableConfig;
    }

    private void saveTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) {
        try {
            tableConfigManager.saveTableConfiguration(table, configuration);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: display error
        }
    }

    private void onBigtableTableSelected(ObservableValue<? extends BigtableTable> observable, BigtableTable oldValue, BigtableTable newValue) {
        bigtableTableView.clear();
        tableNameLabel.setText(newValue.getSimpleName());
        tableNameLabel.setVisible(true);
        rowSelectionView.setVisible(true);

        BigtableReadRequest request = new BigtableReadRequestBuilder()
                .setCredentialsPath(credentialsManager.getCredentialsPath())
                .setTable(newValue)
                .setRowRange(BigtableRowRange.DEFAULT)
                .build();

        readBigtableRows(request);
    }

    private void readBigtableRows(BigtableReadRequest request) {
        ReadBigtableRows readRowsService = new ReadBigtableRows(bigtableClient, request);
        readRowsService.setOnSucceeded(workerStateEvent -> {
            bigtableTableView.setVisible(true);
            rowSelectionView.getProgressBar().setVisible(false);
            readRowsService.getValue().forEach(row -> bigtableTableView.add(row));
        });
        rowSelectionView.getProgressBar().setVisible(true);
        rowSelectionView.getProgressBar().progressProperty().bind(readRowsService.progressProperty());
        readRowsService.start();
    }
}
