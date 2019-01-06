package com.erikmafo.btviewer.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class SpecifyCredentialsPathDialog extends DialogPane {

    @FXML
    private TextField credentialsPathTextField;

    public SpecifyCredentialsPathDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/specify_credentials_path_dialog.fxml"));

        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void handleEditCredentialsPathAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("json files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file != null)
        {
            credentialsPathTextField.setText(file.getPath());
        }

    }

    public static CompletableFuture<Path> displayAndAwaitResult(Path currentPath) {

        CompletableFuture<Path> result = new CompletableFuture<>();

        Dialog<String> dialog = new Dialog<>();
        SpecifyCredentialsPathDialog credentialsPathDialog = new SpecifyCredentialsPathDialog();

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

        dialog.setOnHidden(event1 -> {
            String pathAsString = dialog.getResult();
            if (pathAsString != null) {
                try {
                    Path path = Path.of(pathAsString);
                    if (Files.exists(path)) {
                        if (Files.isReadable(path)) {
                            result.complete(path);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Invalid path");
                            alert.setHeaderText("Please specify a valid path");
                            alert.setContentText("File is not readable");
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Invalid path");
                        alert.setHeaderText("Please specify a valid path");
                        alert.setContentText("File not found");
                        alert.showAndWait();
                    }
                } catch (InvalidPathException e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invalid path");
                    alert.setHeaderText("Please specify a valid path");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });

        dialog.show();

        return result;
    }

}
