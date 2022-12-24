package com.erikmafo.btviewer.ui;

import com.erikmafo.btviewer.MainApp;
import com.erikmafo.btviewer.config.AppConfig;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class MainAppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        AppConfig appConfig = new AppConfig();
        appConfig.setUseBigtableEmulator(true);
        appConfig.setUseInMemoryDatabase(true);
        MainApp mainApp = new MainApp();
        mainApp.setAppConfig(appConfig);
        mainApp.start(stage);
    }

    @Test
    public void shouldStart() {
        verifyThat("#addInstanceButton", isVisible());
        verifyThat("#projectExplorer", isVisible());
        verifyThat("#menuBar", isVisible());
    }

    @Test
    public void shouldOpenAddInstanceDialog() {
        // when:
        clickOn("#addInstanceButton", MouseButton.PRIMARY);

        // then:
        verifyThat(lookup(".dialog-pane").queryAs(DialogPane.class), isVisible());
    }
}
