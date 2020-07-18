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
import com.erikmafo.btviewer.util.AlertUtil;
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

    private final SaveTableSettingsService saveTableSettingsService;
    private final LoadTableSettingsService loadTableSettingsService;
    private final ReadRowsService readRowsService;

    @Inject
    public MainController(
            SaveTableSettingsService saveTableSettingsService,
            LoadTableSettingsService loadTableSettingsService,
            ReadRowsService readRowsService) {
        this.saveTableSettingsService = saveTableSettingsService;
        this.loadTableSettingsService = loadTableSettingsService;
        this.readRowsService = readRowsService;
    }

    public void initialize() {
        queryBox.setVisible(false);
        bigtableTableView.setVisible(false);
        bigtableTableView.setOnTableSettingsChanged(this::onTableSettingsChanged);
        projectExplorerController.selectedTableProperty().addListener(this::onBigtableTableSelected);
        queryBox.setOnExecuteQuery(this::onExecuteQuery);
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

    private void loadTableConfiguration(BigtableTable currentTable, Consumer<BigtableTableSettings> configurationConsumer) {
        loadTableSettingsService.setTable(currentTable);
        loadTableSettingsService.setOnSucceeded(event -> {
            var tableConfig = loadTableSettingsService.getValue();
            bigtableTableView.setValueConverter(BigtableValueConverter.from(tableConfig));
            configurationConsumer.accept(tableConfig);
        });
        loadTableSettingsService.setOnFailed(event -> AlertUtil.displayError("Unable to load table configuration", event));
        loadTableSettingsService.restart();
    }

    private void onTableSettingsChanged(ActionEvent event) {
        var table = projectExplorerController.selectedTableProperty().get();
        loadTableSettingsService.setTable(table);
        loadTableSettingsService.setOnSucceeded(e -> TableSettingsDialog
                .displayAndAwaitResult(bigtableTableView.getColumns(), loadTableSettingsService.getValue())
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table, configuration))
        );
        loadTableSettingsService.setOnFailed(e -> TableSettingsDialog
                .displayAndAwaitResult(bigtableTableView.getColumns(), new BigtableTableSettings())
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table, configuration))
        );
        loadTableSettingsService.restart();
    }

    private void updateTableConfiguration(BigtableTable table, BigtableTableSettings configuration) {
        bigtableTableView.setValueConverter(new BigtableValueConverter(configuration.getCellDefinitions()));
        saveTableConfiguration(table, configuration);
    }

    private void saveTableConfiguration(BigtableTable table, BigtableTableSettings configuration) {
        saveTableSettingsService.setTableConfiguration(table, configuration);
        saveTableSettingsService.setOnFailed(event -> AlertUtil.displayError("Failed to save table configuration", event));
        saveTableSettingsService.restart();
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
            AlertUtil.displayError("Failed to execute query: ", stateEvent);
        });
        queryBox.getProgressBar().setVisible(true);
        queryBox.getProgressBar().progressProperty().bind(readRowsService.progressProperty());
        readRowsService.restart();
    }
}
