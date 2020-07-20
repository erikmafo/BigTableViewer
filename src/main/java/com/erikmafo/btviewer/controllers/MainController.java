package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.*;
import com.erikmafo.btviewer.projectexplorer.ProjectExplorerController;
import com.erikmafo.btviewer.sql.SqlQuery;
import javafx.fxml.FXML;

/**
 * Created by erikmafo on 23.12.17.
 */
public class MainController {

    @FXML
    private QueryBoxController queryBoxController;

    @FXML
    private ProjectExplorerController projectExplorerController;

    @FXML
    private BigtableViewController bigtableViewController;

    @FXML
    private void initialize() {
        bigtableViewController.tableProperty().bind(queryBoxController.tableProperty());
        bigtableViewController.setRows(queryBoxController.getQueryResult());
        queryBoxController.instanceProperty().bind(projectExplorerController.selectedInstanceProperty());
        projectExplorerController
                .selectedTableProperty()
                .addListener((obs, prev, current) ->
                        queryBoxController.setQuery(SqlQuery.getDefaultSql(current.getTableId())));
    }
}
