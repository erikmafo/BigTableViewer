package com.erikmafo.btviewer.ui.dialogs;

import com.erikmafo.btviewer.util.FXMLLoaderUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class CredentialsPathDialog extends DialogPane {

    @FXML
    private TextField credentialsPathTextField;

    public CredentialsPathDialog() {
        FXMLLoaderUtil.loadFxml("/fxml/credentials_path_dialog.fxml", this);
    }

    @NotNull
    public static CompletableFuture<Path> displayAndAwaitResult(@Nullable Path currentPath) {
        CompletableFuture<Path> result = new CompletableFuture<>();
        Dialog<String> dialog = new Dialog<>();
        var credentialsPathDialog = new CredentialsPathDialog();

        if (currentPath != null) {
            credentialsPathDialog.credentialsPathTextField.textProperty().setValue(currentPath.toString());
        }

        dialog.setDialogPane(credentialsPathDialog);
        dialog.setResultConverter(param -> {
            if (ButtonBar.ButtonData.OK_DONE.equals(param.getButtonData())) {
                return credentialsPathDialog.credentialsPathTextField.textProperty().get();
            }
            return null;
        });
        dialog.setOnHidden(ignore -> {
            var pathAsString = dialog.getResult();
            if (FilePathValidatorUtil.validatePath(pathAsString)) {
                result.complete(Path.of(pathAsString));
            }
        });
        dialog.show();

        return result;
    }

    @FXML
    public void handleEditCredentialsPathAction(ActionEvent event) {
        var fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        var file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null)
        {
            credentialsPathTextField.setText(file.getPath());
        }
    }
}
