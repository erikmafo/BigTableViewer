package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.model.BigtableValueParser;
import com.erikmafo.btviewer.model.CredentialsRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import javax.inject.Inject;
import java.util.List;

public class BigtableMenuBarController {

    @Inject
    public BigtableMenuBarController() {
    }

    @FXML
    private Menu selectTableMenu;

    @FXML
    private MenuItem credentialsMenu;

    public void handleManageCredentialsAction(ActionEvent event) {

    }

    public void handleCreateNewTableAction(ActionEvent event) {

    }

    public void addBigtableDefinitions(List<BigtableValueParser> bigtableDefinitions) {
        //bigtableDefinitions.forEach(this::addBigtableDefinition);
    }

    public void addCredentialRecords(List<CredentialsRecord> records) {

    }
}
