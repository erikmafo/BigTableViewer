package com.erikmafo.btviewer.ui.util;

import javafx.scene.control.Alert;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class FilePathValidatorUtil {
    private static final String FILE_IS_NOT_READABLE = "File is not readable";
    private static final String FILE_NOT_FOUND = "File not found";

    public static boolean validatePath(@Nullable String pathAsString) {
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
