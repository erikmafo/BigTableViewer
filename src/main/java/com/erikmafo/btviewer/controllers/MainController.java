package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.*;
import com.erikmafo.btviewer.events.ExecuteQueryAction;
import com.erikmafo.btviewer.events.InstanceTreeItemExpanded;
import com.erikmafo.btviewer.events.ProjectTreeItemExpanded;
import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.projectexplorer.ProjectExplorer;
import com.erikmafo.btviewer.services.*;
import com.erikmafo.btviewer.sql.QueryConverter;
import com.erikmafo.btviewer.sql.SqlQuery;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by erikmafo on 23.12.17.
 */
public class MainController {

    @FXML
    private QueryBox queryBox;

    @FXML
    private ProjectExplorer projectExplorerController;

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
        queryBox.setVisible(false);
        bigtableTableView.setVisible(false);
        bigtableTableView.setOnTableSettingsChanged(this::onTableSettingsChanged);
        projectExplorerController.setOnCreateNewBigtableInstance(this::onAddNewBigtableInstance);
        projectExplorerController.selectedTableProperty().addListener(this::onBigtableTableSelected);
        projectExplorerController.setProjectItemExpandedHandler(this::onProjectItemExpanded);
        projectExplorerController.setInstanceItemExpandedHandler(this::onInstanceItemExpanded);
        queryBox.setOnExecuteQuery(this::onExecuteQuery);
        loadBigtableInstances();
    }

    private void loadBigtableInstances() {
        loadInstancesService.setOnSucceeded(stateEvent ->
                projectExplorerController.addBigtableInstances(loadInstancesService.getValue()));
        loadInstancesService.setOnFailed(stateEvent -> displayErrorInfo("Failed to load instances", stateEvent));
        loadInstancesService.restart();
    }

    private void onAddNewBigtableInstance(ActionEvent event) {
        AddInstanceDialog.displayAndAwaitResult()
                .whenComplete((instance, throwable) -> {
                    if (instance == null) {
                        return;
                    }
                    saveInstance(instance);
                    projectExplorerController.addBigtableInstance(instance);
                    listBigtableTables(instance);
                });
    }

    private void saveInstance(BigtableInstance instance) {
        saveInstancesService.addInstance(instance);
        saveInstancesService.setOnFailed(stateEvent -> displayErrorInfo("Unable to save instance", stateEvent));
        saveInstancesService.restart();
    }

    private void onInstanceItemExpanded(InstanceTreeItemExpanded event) {
        listBigtableTables(event.getInstance());
    }

    private void onProjectItemExpanded(ProjectTreeItemExpanded event) {
        listBigtableTables(event.getInstances());
    }

    private void listBigtableTables(BigtableInstance instance) {
        listBigtableTables(List.of(instance));
    }

    private void listBigtableTables(List<BigtableInstance> instances) {
        if (listTablesService.isRunning()) {
            listTablesService.cancel();
        }
        listTablesService.addInstances(instances);
        listTablesService.setOnSucceeded(workerStateEvent ->
                projectExplorerController.addBigtableTables(listTablesService.getValue()));
        listTablesService.setOnFailed(stateEvent -> displayErrorInfo("Unable to list tables", stateEvent));
        listTablesService.restart();
    }

    private void onExecuteQuery(ExecuteQueryAction queryAction) {
        bigtableTableView.clear();
        var currentInstance = projectExplorerController.selectedInstanceProperty().get();
        var sqlQuery = queryAction.getSqlQuery();
        var table = new BigtableTable(currentInstance, sqlQuery.getTableName());
        loadTableConfiguration(table, tableConfig -> {
            try {
                var queryConverter =
                        new QueryConverter(new ByteStringConverterImpl(tableConfig.getCellDefinitions()));
                var request = new BigtableReadRequestBuilder()
                        .setInstance(currentInstance)
                        .setQuery(queryConverter.toBigtableQuery(sqlQuery))
                        .setLimit(sqlQuery.getLimit())
                        .build();
                readBigtableRows(request);
            } catch (Exception ex) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to convert sql to Bigtable query");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        });
    }

    private void loadTableConfiguration(BigtableTable currentTable, Consumer<BigtableTableConfiguration> configurationConsumer) {
        loadTableConfigurationService.setTable(currentTable);
        loadTableConfigurationService.setOnSucceeded(event -> {
            var tableConfig = loadTableConfigurationService.getValue();
            bigtableTableView.setValueConverter(BigtableValueConverter.from(tableConfig));
            configurationConsumer.accept(tableConfig);
        });
        loadTableConfigurationService.setOnFailed(event -> displayErrorInfo("Unable to load table configuration", event));
        loadTableConfigurationService.restart();
    }

    private void onTableSettingsChanged(ActionEvent event) {
        var table = projectExplorerController.selectedTableProperty().get();
        loadTableConfigurationService.setTable(table);
        loadTableConfigurationService.setOnSucceeded(e -> TableSettingsDialog
                .displayAndAwaitResult(bigtableTableView.getColumns(), loadTableConfigurationService.getValue())
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table, configuration))
        );
        loadTableConfigurationService.setOnFailed(e -> TableSettingsDialog
                .displayAndAwaitResult(bigtableTableView.getColumns(), new BigtableTableConfiguration(table))
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
        queryBox.setVisible(true);
        queryBox.setQuery(SqlQuery.getDefaultSql(newValue.getTableId()));
    }

    private void readBigtableRows(BigtableReadRequest request) {
        readRowsService.setReadRequest(request);
        readRowsService.setOnSucceeded(workerStateEvent -> {
            bigtableTableView.setVisible(true);
            queryBox.getProgressBar().setVisible(false);
            readRowsService.getValue().forEach(row -> bigtableTableView.add(row));
        });
        readRowsService.setOnFailed(stateEvent -> {
            queryBox.getProgressBar().setVisible(false);
            displayErrorInfo("Failed to execute query: ", stateEvent);
        });
        queryBox.getProgressBar().setVisible(true);
        queryBox.getProgressBar().progressProperty().bind(readRowsService.progressProperty());
        readRowsService.restart();
    }
}
