package com.erikmafo.btviewer.ui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

public class ActionEventUtil {

    public static Window getWindow(ActionEvent actionEvent) {
        return getScene(actionEvent).getWindow();
    }

    public static Scene getScene(ActionEvent actionEvent) {
        return getNode(actionEvent).getScene();
    }

    private static Node getNode(@NotNull ActionEvent actionEvent) {
        return (Node) actionEvent.getSource();
    }
}
