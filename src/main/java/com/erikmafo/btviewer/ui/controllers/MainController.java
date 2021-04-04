package com.erikmafo.btviewer.ui.controllers;

import com.erikmafo.btviewer.sql.SqlQuery;
import com.erikmafo.btviewer.ui.components.QueryBoxController;
import com.erikmafo.btviewer.ui.components.QueryResultViewController;
import com.erikmafo.btviewer.ui.projectexplorer.ProjectExplorerController;
import javafx.fxml.FXML;

/**
 * Created by erikmafo on 23.12.17.
 */
public class MainController {

    @FXML
    private MenuBarController menuBarController;

    @FXML
    private QueryBoxController queryBoxController;

    @FXML
    private ProjectExplorerController projectExplorerController;

    @FXML
    private QueryResultViewController queryResultViewController;

    @FXML
    private void initialize() {
        queryResultViewController.tableProperty().bind(queryBoxController.tableProperty());
        queryResultViewController.setRows(queryBoxController.getQueryResult());
        queryBoxController.instanceProperty().bind(projectExplorerController.selectedInstanceProperty());
        projectExplorerController
                .selectedTableProperty()
                .addListener((obs, prev, current) ->
                        queryBoxController.setQuery(SqlQuery.getDefaultSqlQuery(current.getTableId())));
    }
}
