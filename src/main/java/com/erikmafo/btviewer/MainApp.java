package com.erikmafo.btviewer;
import com.gluonhq.ignite.guice.GuiceContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Arrays;


/**
 * Created by erikmafo on 12.12.17.
 */
public class MainApp extends Application  {

    private GuiceContext guiceContext = new GuiceContext(this, () -> Arrays.asList(new AppConfig()));

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        guiceContext.init();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(guiceContext::getInstance);
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
