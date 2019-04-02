package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.*;
import com.erikmafo.btviewer.events.BigtableProjectTreeItemExpanded;
import com.erikmafo.btviewer.events.ScanTableAction;
import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;
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
    private final TableConfigurationManager tableConfigurationManager;
    private final BigtableClient bigtableClient;

    @Inject
    public MainController(CredentialsManager credentialsManager,
                          BigtableInstanceManager bigtableInstanceManager,
                          TableConfigurationManager tableConfigurationManager,
                          BigtableClient bigtableClient) {
        this.credentialsManager = credentialsManager;
        this.bigtableInstanceManager = bigtableInstanceManager;
        this.tableConfigurationManager = tableConfigurationManager;
        this.bigtableClient = bigtableClient;
    }

    public void initialize() {
        rowSelectionView.setVisible(false);
        bigtableTableView.setVisible(false);
        tableNameLabel.setVisible(false);

        bigtableTableView.setOnConfigureRowValuesTypes(this::onConfigureRowValueTypes);
        tablesListView.setOnCreateNewBigtableInstance(this::onAddNewBigtableInstance);
        tablesListView.selectedTableProperty().addListener(this::onBigtableTableSelected);
        tablesListView.setTreeItemExpandedEventHandler(new EventHandler<BigtableProjectTreeItemExpanded>() {
            @Override
            public void handle(BigtableProjectTreeItemExpanded event) {
                event.getBigtableInstances().forEach(MainController.this::listBigtableTables);
            }
        });
        rowSelectionView.setOnScanTable(this::onScanTableAction);


        tablesListView.addBigtableInstances(bigtableInstanceManager.getInstances());
    }

    private void onAddNewBigtableInstance(ActionEvent event) {
        BigtableInstanceDialog.displayAndAwaitResult()
                .whenComplete((instance, throwable) -> {
                    List<BigtableInstance> allInstances = bigtableInstanceManager.getInstances();
                    allInstances.add(instance);
                    bigtableInstanceManager.setInstances(allInstances);

                    tablesListView.addBigtableInstance(instance);
                    listBigtableTables(instance);
                });
    }

    private void listBigtableTables(BigtableInstance instance) {
        ListBigtableTables listBigtableTables = new ListBigtableTables(
                bigtableClient, instance, credentialsManager.getCredentialsPath());
        listBigtableTables.setOnSucceeded(workerStateEvent -> {
            tablesListView.addBigtableTables(listBigtableTables.getValue());
        });
        listBigtableTables.start();
    }

    private void onScanTableAction(ScanTableAction actionEvent) {

        BigtableTable currentTable = tablesListView.selectedTableProperty().get();
        bigtableTableView.clear();
        BigtableReadRequest request = new BigtableReadRequestBuilder()
                .setCredentialsPath(credentialsManager.getCredentialsPath())
                .setTable(currentTable)
                .setRowRange(new BigtableRowRange(actionEvent.getFrom(), actionEvent.getTo()))
                .build();

        readBigtableRows(request);
    }

    private void onConfigureRowValueTypes(ActionEvent event) {
        BigtableValueTypesDialog.displayAndAwaitResult(bigtableTableView.getColumns())
                .whenComplete((configuration, throwable) ->
                {
                    bigtableTableView.setValueConverter(new BigtableValueConverter(configuration.getCellDefinitions()));
                    tableConfigurationManager.saveTableConfiguration(
                            tablesListView.selectedTableProperty().get(),
                            configuration);
                });
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
            readRowsService.getValue()
                    .forEach(row -> bigtableTableView.add(row));

        });
        rowSelectionView.getProgressBar().setVisible(true);
        rowSelectionView.getProgressBar().progressProperty().bind(readRowsService.progressProperty());
        readRowsService.start();
    }
}
