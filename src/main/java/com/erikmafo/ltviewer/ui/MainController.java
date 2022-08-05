package com.erikmafo.ltviewer.ui;

import com.erikmafo.ltviewer.sql.SqlQuery;
import com.erikmafo.ltviewer.ui.menubar.MenuBarController;
import com.erikmafo.ltviewer.ui.projectexplorer.ProjectExplorerController;
import com.erikmafo.ltviewer.ui.querybox.QueryBoxController;
import com.erikmafo.ltviewer.ui.queryresult.QueryResultViewController;
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
    public void initialize() {
        queryResultViewController.tableProperty().bind(queryBoxController.tableProperty());
        queryResultViewController.setRows(queryBoxController.getQueryResult());
        queryBoxController.instanceProperty().bind(projectExplorerController.selectedInstanceProperty());
        projectExplorerController
                .selectedTableProperty()
                .addListener((obs, prev, current) ->
                        queryBoxController.setQuery(SqlQuery.getDefaultSqlQuery(current.getTableId())));
    }
}
