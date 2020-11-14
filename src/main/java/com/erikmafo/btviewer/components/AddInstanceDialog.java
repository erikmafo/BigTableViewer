package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.FXMLLoaderUtil;
import com.erikmafo.btviewer.model.BigtableInstance;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.util.concurrent.CompletableFuture;

public class AddInstanceDialog extends DialogPane {

    @FXML
    private TextField projectIdTextField;

    @FXML
    private TextField instanceIdTextField;

    private AddInstanceDialog() {
        FXMLLoaderUtil.loadFxml("/fxml/add_instance_dialog.fxml", this);
    }

    private BigtableInstance getBigtableInstance() {
        return new BigtableInstance(
                projectIdTextField.getText(),
                instanceIdTextField.getText());
    }

    public void preFillProjectId(String projectId) {
        projectIdTextField.setText(projectId);
        projectIdTextField.setEditable(false);
        instanceIdTextField.requestFocus();
    }

    public static CompletableFuture<BigtableInstance> displayAndAwaitResult() {
        return displayAndAwaitResult(null);
    }

    public static CompletableFuture<BigtableInstance> displayAndAwaitResult(String projectId) {
        CompletableFuture<BigtableInstance> future = new CompletableFuture<>();

        try {
            Dialog<BigtableInstance> dialog = new Dialog<>();
            AddInstanceDialog pane = new AddInstanceDialog();

            if (projectId != null) {
                pane.preFillProjectId(projectId);
            }

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
