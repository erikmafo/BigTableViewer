package com.erikmafo.btviewer.controllers;

import com.erikmafo.btviewer.components.SpecifyCredentialsPathDialog;
import com.erikmafo.btviewer.services.CredentialsManager;
import com.sun.javafx.PlatformUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javax.inject.Inject;
import java.nio.file.Path;

public class MenuBarController {

    private final CredentialsManager credentialsManager;

    @Inject
    public MenuBarController(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    @FXML
    private MenuItem credentialsMenu;

    @FXML
    private MenuBar menuBar;

    public void initialize() {
        if (PlatformUtil.isMac()) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
    }

    public void onManageCredentialsAction(ActionEvent event) {
        Path currentPath = credentialsManager.getCredentialsPath();
        SpecifyCredentialsPathDialog.displayAndAwaitResult(currentPath)
                .whenComplete((newCredentialsPath, throwable) ->
                        credentialsManager.setCredentialsPath(newCredentialsPath));
    }
}
