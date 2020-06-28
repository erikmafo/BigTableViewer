package com.erikmafo.btviewer;
import com.erikmafo.btviewer.services.*;
import com.google.inject.Guice;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by erikmafo on 12.12.17.
 */
public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(Guice.createInjector(new ServicesModule())::getInstance);
        Parent root = loader.load();
        primaryStage.setTitle("Bigtable viewer");
        primaryStage.setScene(new Scene(root, 800, 700));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
