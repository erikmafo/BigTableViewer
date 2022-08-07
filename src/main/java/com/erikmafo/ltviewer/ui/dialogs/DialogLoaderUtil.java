package com.erikmafo.ltviewer.ui.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DialogLoaderUtil {

    @NotNull
    public static <T> CompletableFuture<T> displayDialogAndAwaitResult(T initialValue, String fxmlFile) {
        CompletableFuture<T> result = new CompletableFuture<>();
        Dialog<T> dialog = new Dialog<>();
        var protoObjectDialogLoader =  getLoader(fxmlFile);
        DialogPane protoObjectDialog = null;
        try {
            protoObjectDialog = protoObjectDialogLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DialogController<T> controller = protoObjectDialogLoader.getController();

        if (initialValue != null) {
            controller.setResult(initialValue);
        }

        dialog.setDialogPane(protoObjectDialog);
        dialog.setResultConverter(param -> {
            if (ButtonBar.ButtonData.OK_DONE.equals(param.getButtonData())) {
                return controller.getResult();
            }
            return null;
        });
        dialog.setOnHidden(ignore -> {
            var resultValue = dialog.getResult();
            if (controller.validateResult(resultValue)) {
                result.complete(resultValue);
            }
        });
        dialog.show();

        return result;
    }

    @NotNull
    @Contract(" -> new")
    public static FXMLLoader getLoader(String fxmlFile) {
        return new FXMLLoader(ProtoObjectDialogController.class.getClassLoader().getResource(fxmlFile));
    }
}
