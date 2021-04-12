package com.erikmafo.btviewer.ui.projectexplorer;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.util.FXMLLoaderUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AddInstanceDialog extends DialogPane {

    @FXML
    private TextField projectIdTextField;

    @FXML
    private TextField instanceIdTextField;

    private AddInstanceDialog() {
        FXMLLoaderUtil.loadFxml("/fxml/add_instance_dialog.fxml", this);
    }

    @NotNull
    public static CompletableFuture<BigtableInstance> displayAndAwaitResult() {
        return displayAndAwaitResult(null);
    }

    @NotNull
    public static CompletableFuture<BigtableInstance> displayAndAwaitResult(@Nullable String projectId) {
        CompletableFuture<BigtableInstance> future = new CompletableFuture<>();

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

        return future;
    }

    public void preFillProjectId(String projectId) {
        projectIdTextField.setText(projectId);
        projectIdTextField.setEditable(false);
        instanceIdTextField.requestFocus();
    }

    @NotNull
    @Contract(" -> new")
    private BigtableInstance getBigtableInstance() {
        return new BigtableInstance(
                projectIdTextField.getText(),
                instanceIdTextField.getText());
    }
}
