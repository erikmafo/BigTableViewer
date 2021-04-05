package com.erikmafo.btviewer.ui;

import com.erikmafo.btviewer.sql.SqlQuery;
import com.erikmafo.btviewer.ui.menubar.MenuBarController;
import com.erikmafo.btviewer.ui.projectexplorer.ProjectExplorerController;
import com.erikmafo.btviewer.ui.querybox.QueryBoxController;
import com.erikmafo.btviewer.ui.queryresult.QueryResultViewController;
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
