package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.BigtableInstanceDialog;
import com.erikmafo.btviewer.components.BigtableValueTypesDialog;
import com.erikmafo.btviewer.components.BigtableTablesListView;
import com.erikmafo.btviewer.components.BigtableView;
import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.BigtableClient;
import com.erikmafo.btviewer.services.BigtableResultScanner;
import com.erikmafo.btviewer.services.UserConfigurationService;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 23.12.17.
 */
public class MainController {

    @FXML
    private BigtableTablesListView tablesListView;

    @FXML
    private MenuBar bigtableMenuBar;

    @FXML
    private BigtableMenuBarController bigtableMenuBarController;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private VBox mainView;

    @FXML
    private TextField fromTextField;

    @FXML
    private TextField toTextField;

    @FXML
    private Label tableNameLabel;

    @FXML
    private BigtableView bigtableView;

    private  final FetchBigtableRowsService fetchBigtableRowsService;
    private final UserConfigurationService userConfigurationService;
    private final ObservableList<BigtableRow> bigtableRows = new ObservableListWrapper<>(new ArrayList<>());
    private final BigtableClient bigtableClient;

    @Inject
    public MainController(BigtableClient bigtableClient, UserConfigurationService userConfigurationService) {
        this.fetchBigtableRowsService = new FetchBigtableRowsService(bigtableClient);
        this.userConfigurationService = userConfigurationService;
        this.bigtableClient = bigtableClient;
    }

    public void initialize() {

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            bigtableMenuBar.useSystemMenuBarProperty().set(true);
        }

        mainView.setVisible(false);
        progressBar.progressProperty().bind(fetchBigtableRowsService.progressProperty());
        progressBar.visibleProperty().bind(fetchBigtableRowsService.runningProperty());

        fetchBigtableRowsService.setOnSucceeded(event -> {
            mainView.setVisible(true);
            List<BigtableRow> rows = fetchBigtableRowsService.getValue();
            bigtableRows.setAll(rows);
        });

        tablesListView.selectedTableProperty().addListener((observable, oldValue, newValue) ->
        {
            bigtableRows.clear();
            mainView.setVisible(true);
            tableNameLabel.setText(newValue.getSimpleName());
            BigtableReadRequest request = new BigtableReadRequestBuilder()
                    .setCredentialsPath(userConfigurationService.getCredentialsPath())
                    .setBigtableTable(newValue)
                    .setScan(new BigtableRowRange("", "~", 100))
                    .build();
            try {
                bigtableView.setBigtableScanner(bigtableClient.execute(request));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tablesListView.setOnAddBigtableInstance(event -> {
            BigtableInstanceDialog.displayAndAwaitResult()
                    .whenComplete((instance, throwable) -> {
                            tablesListView.addBigtableInstance(instance, null);
                            Platform.runLater(() -> tablesListView.addBigtableTables(tryListBigtableTables(instance)));
                    });
        });
    }

    private List<BigtableTable> tryListBigtableTables(BigtableInstance instance) {
        try {
            return bigtableClient.listTables(
                    instance,
                    userConfigurationService.getCredentialsPath())
                    .stream()
                    .map(this::getBigtableTable)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private List<BigtableInstance> tryListBigtableInstances(String projectId) {
        try {
            return bigtableClient.listInstances(projectId, userConfigurationService.getCredentialsPath());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public void handleScanTableAction(ActionEvent actionEvent) {

        BigtableReadRequest request = new BigtableReadRequestBuilder()
                .setCredentialsPath(userConfigurationService.getCredentialsPath())
                .setBigtableTable(tablesListView.selectedTableProperty().get())
                .setScan(new BigtableRowRange(fromTextField.getText(), toTextField.getText()))
                .build();

        try {
            bigtableView.setBigtableScanner(bigtableClient.execute(request));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fetchBigtableRowsService.setReadRequest(request);
        //fetchBigtableRowsService.restart();
    }

    private BigtableTable getBigtableTable(String tableName) {
        return userConfigurationService
                .loadBigtableDefinitions()
                .stream()
                .filter(def -> tableName.equals(def.getName()))
                .findFirst().orElse(new BigtableTable(tableName));
    }

    public void handleConfigureValueParserAction(ActionEvent event) {
        BigtableValueTypesDialog.displayAndAwaitResult(bigtableView.getColumns());
    }

    private static class FetchBigtableRowsService extends Service<List<BigtableRow>> {

        private final BigtableClient bigtableClient;

        private BigtableReadRequest readRequest;

        public FetchBigtableRowsService(BigtableClient bigtableClient) {
            this.bigtableClient = bigtableClient;
        }

        public void setReadRequest(BigtableReadRequest readRequest) {
            this.readRequest = readRequest;
        }

        @Override
        protected Task<List<BigtableRow>> createTask() {

            return new Task<List<BigtableRow>>() {

                @Override
                protected List<BigtableRow> call() throws Exception {

                    List<BigtableRow> rows = new LinkedList<>();

                    int count = 0;

                    try(BigtableResultScanner scanner = bigtableClient.execute(readRequest)) {

                        BigtableRow row = scanner.next();

                        while (row != null) {
                            rows.add(row);
                            count++;
                            updateProgress(count, readRequest.getScan().getMaxRows());
                            row = scanner.next();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }

                    return rows;
                }

            };
        }
    }
}
