package com.erikmafo.ltviewer.util;

import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;

public class AlertUtil {

    public static void displayError(String errorText, @NotNull WorkerStateEvent event) {
        displayError(errorText, event.getSource().getException());
    }

    public static void displayError(String errorText, Throwable cause) {
        var alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.CLOSE);
        alert.setTitle("Computer says no!");
        alert.setHeaderText(errorText);
        if (cause != null) {
            alert.setContentText(cause.toString());
        }

        alert.showAndWait();
    }
}
