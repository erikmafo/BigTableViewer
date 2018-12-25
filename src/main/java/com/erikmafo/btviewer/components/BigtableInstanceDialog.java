package com.erikmafo.btviewer.components;
import com.erikmafo.btviewer.model.BigtableInstance;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class BigtableInstanceDialog extends DialogPane {

    @FXML
    private TextField projectIdTextField;

    @FXML
    private TextField instanceIdTextField;

    private BigtableInstanceDialog() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bigtable_instance_dialog.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BigtableInstance getBigtableInstance() {
        return new BigtableInstance(
                projectIdTextField.getText(),
                instanceIdTextField.getText());
    }

    public static CompletableFuture<BigtableInstance> displayAndAwaitResult() {
        CompletableFuture<BigtableInstance> future = new CompletableFuture<>();

        try {
            Dialog<BigtableInstance> dialog = new Dialog<>();
            BigtableInstanceDialog pane = new BigtableInstanceDialog();
            dialog.setDialogPane(pane);
            dialog.setResultConverter(buttonType -> {

                if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {
                    return pane.getBigtableInstance();
                }

                return null;
            });
            dialog.setOnHidden(event -> future.complete(dialog.getResult()));
            dialog.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return future;
    }
}
