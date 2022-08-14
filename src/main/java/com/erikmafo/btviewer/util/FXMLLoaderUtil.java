package com.erikmafo.btviewer.util;
import javafx.fxml.FXMLLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.MessageFormat;

public class FXMLLoaderUtil {

    public static void loadFxml(String fxmlFile, @NotNull Object controller) {
        var loader = new FXMLLoader(controller.getClass().getResource(fxmlFile));
        loader.setRoot(controller);
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    MessageFormat.format("The provided fxml file: {0} could not be loaded into controller{1}", fxmlFile, controller.getClass().getName()), e);
        }
    }

    public static <T> T loadFxml(String fxmlFile, @NotNull Class<T> controllerType) {
        var loader = new FXMLLoader(controllerType.getClassLoader().getResource(fxmlFile));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    MessageFormat.format("The provided fxml file: {0} could not be loaded", fxmlFile), e);
        }
    }
}
