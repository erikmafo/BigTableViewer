package com.erikmafo.little.table.viewer.ui.querybox;

import com.erikmafo.little.table.viewer.config.AppConfig;
import com.erikmafo.little.table.viewer.services.ServicesModule;
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