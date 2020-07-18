package com.erikmafo.btviewer.util;

import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertUtil {

    public static void displayError(String errorText, WorkerStateEvent event) {
        displayError(errorText, event.getSource().getException());
    }

    public static void displayError(String errorText, Throwable cause) {
        var alert = new Alert(Alert.AlertType.ERROR, errorText + ": " + cause.getLocalizedMessage(), ButtonType.CLOSE);
        alert.setTitle("Computer says no..");
        alert.showAndWait();
    }
}
