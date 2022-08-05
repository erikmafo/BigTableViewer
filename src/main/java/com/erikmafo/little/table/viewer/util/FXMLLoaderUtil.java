package com.erikmafo.little.table.viewer.util;
import javafx.fxml.FXMLLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FXMLLoaderUtil {

    public static void loadFxml(String fxmlFile, @NotNull Object controller) {
        var loader = new FXMLLoader(controller.getClass().getResource(fxmlFile));
        loader.setRoot(controller);
        loader.setController(controller);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "The provided fxml file: " +
                            fxmlFile + " " +
                            "could not be loaded into controller" +
                            controller.getClass().getName(), e);
        }
    }
}
