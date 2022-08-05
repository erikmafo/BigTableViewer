package com.erikmafo.ltviewer.ui.querybox;

import com.erikmafo.ltviewer.config.AppConfig;
import com.erikmafo.ltviewer.services.ServicesModule;
import com.google.inject.Guice;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class QueryBoxControllerTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        var appConfig = new AppConfig();
        appConfig.setUseBigtableEmulator(true);
        appConfig.setUseInMemoryDatabase(true);
        var loader = new FXMLLoader(getClass().getResource("/fxml/query_box.fxml"));
        var injector = Guice.createInjector(new ServicesModule(appConfig));
        loader.setControllerFactory(injector::getInstance);
        stage.setScene(new Scene(loader.load(), 800, 600));
        loader.getController();
        stage.show();
    }

    @Test
    public void shouldCreate() {
        verifyThat("#codeArea", isVisible());
        verifyThat("#executeQueryButton", isVisible().and(Node::isDisabled));
        verifyThat("#cancelQueryButton", isVisible().and(Node::isDisabled));
    }

    @Test
    public void queryTextShouldEnableExecuteButton() {
        //when
        clickOn("#codeArea");
        write("SELECT * FROM table-0");

        //then
        verifyThat("#executeQueryButton", button -> !button.isDisabled());
    }
}