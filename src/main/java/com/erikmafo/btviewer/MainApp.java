package com.erikmafo.btviewer;
import com.erikmafo.btviewer.services.BigtableClient;
import com.erikmafo.btviewer.services.BigtableInstanceManager;
import com.erikmafo.btviewer.services.CredentialsManager;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Created by erikmafo on 12.12.17.
 */
public class MainApp extends Application implements Module {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(Guice.createInjector(this)::getInstance);
        Parent root = loader.load();
        primaryStage.setTitle("Bigtable viewer");
        primaryStage.setScene(new Scene(root, 800, 700));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(BigtableClient.class).toInstance(new BigtableClient());
        binder.bind(CredentialsManager.class).toInstance(new CredentialsManager());
        binder.bind(BigtableInstanceManager.class).toInstance(new BigtableInstanceManager());
    }
}
