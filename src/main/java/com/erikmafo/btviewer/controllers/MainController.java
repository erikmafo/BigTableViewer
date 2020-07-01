package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.*;
import com.erikmafo.btviewer.events.ScanTableAction;
import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import javax.inject.Inject;

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

    private final SaveInstancesService saveInstancesService;
    private final LoadInstancesService loadInstancesService;
    private final SaveTableConfigurationService saveTableConfigurationService;
    private final LoadTableConfigurationService loadTableConfigurationService;
    private final ReadRowsService readRowsService;
    private final ListTablesService listTablesService;

    @Inject
    public MainController(
            SaveInstancesService saveInstancesService,
            LoadInstancesService loadInstancesService,
            SaveTableConfigurationService saveTableConfigurationService,
            LoadTableConfigurationService loadTableConfigurationService,
            ReadRowsService readRowsService,
            ListTablesService listTablesService) {
        this.saveInstancesService = saveInstancesService;
        this.loadInstancesService = loadInstancesService;
        this.saveTableConfigurationService = saveTableConfigurationService;
        this.loadTableConfigurationService = loadTableConfigurationService;
        this.readRowsService = readRowsService;
        this.listTablesService = listTablesService;
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
        loadInstancesService.setOnSucceeded(stateEvent ->
                tablesListView.addBigtableInstances(loadInstancesService.getValue()));
        loadInstancesService.setOnFailed(stateEvent -> displayErrorInfo("Failed to load instances", stateEvent));
        loadInstancesService.restart();
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
        saveInstancesService.addInstance(instance);
        saveInstancesService.setOnFailed(stateEvent -> displayErrorInfo("Unable to save instance", stateEvent));
        saveInstancesService.restart();
    }

    private void listBigtableTables(BigtableInstance instance) {
        listTablesService.setInstance(instance);
        listTablesService.setOnSucceeded(workerStateEvent ->
                tablesListView.addBigtableTables(listTablesService.getValue()));
        listTablesService.setOnFailed(stateEvent -> displayErrorInfo("Unable to list tables", stateEvent));
        listTablesService.restart();
    }

    private void onScanTableAction(ScanTableAction actionEvent) {
        bigtableTableView.clear();
        var currentTable = tablesListView.selectedTableProperty().get();
        loadTableConfiguration(currentTable);
        var request = new BigtableReadRequestBuilder()
                .setTable(currentTable)
                .setPrefix(actionEvent.getPrefix())
                .setRowRange(new BigtableRowRange(actionEvent.getFrom(), actionEvent.getTo()))
                .build();
        readBigtableRows(request);
    }

    private void loadTableConfiguration(BigtableTable currentTable) {
        loadTableConfigurationService.setTable(currentTable);
        loadTableConfigurationService.setOnSucceeded(event -> bigtableTableView.setValueConverter(
                new BigtableValueConverter(loadTableConfigurationService.getValue().getCellDefinitions())));
        loadTableConfigurationService.setOnFailed(event -> displayErrorInfo("Unable to load table configuration", event));
        loadTableConfigurationService.restart();
    }

    private void onConfigureRowValueTypes(ActionEvent event) {
        var table = tablesListView.selectedTableProperty().get();
        loadTableConfigurationService.setTable(table);
        loadTableConfigurationService.setOnSucceeded(e -> BigtableValueTypesDialog
                .displayAndAwaitResult(bigtableTableView.getColumns(), loadTableConfigurationService.getValue())
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table, configuration))
        );
        loadTableConfigurationService.setOnFailed(e -> BigtableValueTypesDialog
                .displayAndAwaitResult(bigtableTableView.getColumns(), null)
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table, configuration))
        );
        loadTableConfigurationService.restart();
    }

    private void updateTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) {
        bigtableTableView.setValueConverter(new BigtableValueConverter(configuration.getCellDefinitions()));
        saveTableConfiguration(table, configuration);
    }

    private void saveTableConfiguration(BigtableTable table, BigtableTableConfiguration configuration) {
        saveTableConfigurationService.setTableConfiguration(table, configuration);
        saveTableConfigurationService.setOnFailed(event -> displayErrorInfo("Failed to save table configuration", event));
        saveTableConfigurationService.restart();
    }

    private void displayErrorInfo(String errorText, WorkerStateEvent event) {
        var exception = event.getSource().getException();
        var alert = new Alert(Alert.AlertType.ERROR, errorText + " " + exception.getLocalizedMessage(), ButtonType.CLOSE);
        alert.showAndWait();
    }

    private void onBigtableTableSelected(ObservableValue<? extends BigtableTable> observable, BigtableTable oldValue, BigtableTable newValue) {
        bigtableTableView.clear();
        tableNameLabel.setText(newValue.getSimpleName());
        tableNameLabel.setVisible(true);
        rowSelectionView.setVisible(true);
        readBigtableRows(createReadRequest(newValue));
    }

    private BigtableReadRequest createReadRequest(BigtableTable newValue) {
        return new BigtableReadRequestBuilder()
                    .setTable(newValue)
                    .setRowRange(BigtableRowRange.DEFAULT)
                    .build();
    }

    private void readBigtableRows(BigtableReadRequest request) {
        readRowsService.setReadRequest(request);
        readRowsService.setOnSucceeded(workerStateEvent -> {
            bigtableTableView.setVisible(true);
            rowSelectionView.getProgressBar().setVisible(false);
            loadTableConfiguration(request.getTable());
            readRowsService.getValue().forEach(row -> bigtableTableView.add(row));
        });
        readRowsService.setOnFailed(stateEvent -> displayErrorInfo("Unable to read bigtable rows", stateEvent));
        rowSelectionView.getProgressBar().setVisible(true);
        rowSelectionView.getProgressBar().progressProperty().bind(readRowsService.progressProperty());
        readRowsService.restart();
    }
}
