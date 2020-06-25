package com.erikmafo.btviewer;
import com.erikmafo.btviewer.config.AppConfig;
import com.erikmafo.btviewer.config.ApplicationEnvironment;
import com.erikmafo.btviewer.services.*;
import com.erikmafo.btviewer.services.inmemory.InMemoryBigtableClient;
import com.erikmafo.btviewer.services.inmemory.InMemoryInstanceManager;
import com.erikmafo.btviewer.services.inmemory.InMemoryTableConfigManager;
import com.erikmafo.btviewer.services.inmemory.TestDataUtil;
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

        var config = AppConfig.load(ApplicationEnvironment.get());

        if (config.useInMemoryBigtableClient()) {
            var inMemoryBigtableClient = new InMemoryBigtableClient();
            TestDataUtil.injectWithTestData(inMemoryBigtableClient);
            binder.bind(BigtableClient.class).toInstance(inMemoryBigtableClient);
        }
        else {
            binder.bind(BigtableClient.class).toInstance(new BigtableClientImpl());
        }

        if (config.useInMemoryTableConfigManager()) {
            var inMemoryTableConfigManager = new InMemoryTableConfigManager();
            binder.bind(TableConfigManager.class).toInstance(inMemoryTableConfigManager);
        }
        else {
            binder.bind(TableConfigManager.class).toInstance(new TableConfigManagerImpl());
        }

        if (config.useInMemoryInstanceManager()) {
            var inMemoryInstanceManager = new InMemoryInstanceManager();
            TestDataUtil.injectWithTestData(inMemoryInstanceManager);
            binder.bind(BigtableInstanceManager.class).toInstance(inMemoryInstanceManager);
        } else {
            binder.bind(BigtableInstanceManager.class).toInstance(new BigtableInstanceManagerImpl());
        }

        binder.bind(CredentialsManager.class).toInstance(new CredentialsManager());
    }
}
