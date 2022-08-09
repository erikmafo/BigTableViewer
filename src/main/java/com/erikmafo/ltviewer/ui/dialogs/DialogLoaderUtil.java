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
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        var fxmlLoader =  getLoader(fxmlFile);
        DialogPane dialogPane = getDialogPane(fxmlLoader);
        DialogController<T> controller = getDialogController(initialValue, fxmlLoader);
        var dialog = createDialog(completableFuture, dialogPane, controller);
        dialog.show();

        return completableFuture;
    }

    @NotNull
    private static <T> Dialog<T> createDialog(CompletableFuture<T> completableFuture, DialogPane dialogPane, DialogController<T> controller) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setResultConverter(param -> {
            if (ButtonBar.ButtonData.OK_DONE.equals(param.getButtonData())) {
                return controller.getResult();
            }
            return null;
        });
        dialog.setOnHidden(ignore -> {
            var resultValue = controller.getResult();
            if (resultValue == null || controller.validateResult(resultValue)) {
                completableFuture.complete(resultValue);
            }
        });
        return dialog;
    }

    private static <T> DialogController<T> getDialogController(T initialValue, @NotNull FXMLLoader fxmlLoader) {
        DialogController<T> controller = fxmlLoader.getController();

        if (initialValue != null) {
            controller.setResult(initialValue);
        }
        return controller;
    }

    private static DialogPane getDialogPane(@NotNull FXMLLoader fxmlLoader) {
        DialogPane dialogPane = null;
        try {
            dialogPane = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dialogPane;
    }

    @NotNull
    @Contract(" -> new")
    public static FXMLLoader getLoader(String fxmlFile) {
        return new FXMLLoader(ProtoObjectDialogController.class.getClassLoader().getResource(fxmlFile));
    }
}
