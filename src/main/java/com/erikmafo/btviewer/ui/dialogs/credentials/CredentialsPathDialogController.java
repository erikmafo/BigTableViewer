package com.erikmafo.btviewer.ui.dialogs.credentials;

import com.erikmafo.btviewer.ui.util.ActionEventUtil;
import com.erikmafo.btviewer.ui.shared.DialogController;
import com.erikmafo.btviewer.ui.util.FilePathValidatorUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class CredentialsPathDialogController implements DialogController<Path> {

    public static final String FXML = "fxml/credentials_path_dialog.fxml";
    public static final FileChooser.ExtensionFilter EXTENSION_FILTER = new FileChooser.ExtensionFilter("json files (*.json)", "*.json");

    @FXML
    private TextField credentialsPathTextField;

    @Override
    public void setInitialValue(@NotNull Path path) {
        credentialsPathTextField.setText(path.toString());
    }

    @Override
    public Path getResult() {
        return Path.of(credentialsPathTextField.getText());
    }

    @Override
    public boolean validateResult(@NotNull Path path) {
        return FilePathValidatorUtil.validatePath(path.toString());
    }

    @FXML
    public void handleEditCredentialsPathAction(ActionEvent event) {
        var file = getFileChooser().showOpenDialog(ActionEventUtil.getWindow(event));
        if (file != null)
        {
            credentialsPathTextField.setText(file.getPath());
        }
    }

    @NotNull
    private static FileChooser getFileChooser() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(EXTENSION_FILTER);
        return fileChooser;
    }
}
