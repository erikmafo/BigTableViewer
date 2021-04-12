package com.erikmafo.btviewer.ui.shared;

import com.erikmafo.btviewer.util.FXMLLoaderUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class CredentialsPathDialog extends DialogPane {

    private static final String FILE_IS_NOT_READABLE = "File is not readable";
    private static final String FILE_NOT_FOUND = "File not found";

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
            if (validatePath(pathAsString)) {
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

    private static boolean validatePath(@Nullable String pathAsString) {
        if (pathAsString == null) {
            return false;
        }

        try {
            var path = Path.of(pathAsString);
            if (Files.exists(path)) {
                if (Files.isReadable(path)) {
                    return true;
                } else {
                    showInvalidPathAlert(FILE_IS_NOT_READABLE);
                }
            } else {
                showInvalidPathAlert(FILE_NOT_FOUND);
            }
        } catch (InvalidPathException e) {
            showInvalidPathAlert(e.getMessage());
        }

        return false;
    }

    private static void showInvalidPathAlert(String fileIsNotReadable) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid path");
        alert.setHeaderText("Please specify a valid path");
        alert.setContentText(fileIsNotReadable);
        alert.showAndWait();
    }
}
