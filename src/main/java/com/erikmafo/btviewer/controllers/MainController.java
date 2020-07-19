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

    public void initialize() {
        queryBoxController.instanceProperty().bind(projectExplorerController.selectedInstanceProperty());
        bigtableViewController.tableProperty().bind(queryBoxController.tableProperty());
        queryBoxController.queryResultProperty().addListener((obs, prev, current) ->
                bigtableViewController.getRows().setAll(current));
        projectExplorerController.selectedTableProperty().addListener((obs, prev, current) ->
                queryBoxController.setQuery(SqlQuery.getDefaultSql(current.getTableId())));
    }
}
