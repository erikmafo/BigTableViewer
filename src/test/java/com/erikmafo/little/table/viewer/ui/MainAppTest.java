package com.erikmafo.little.table.viewer.ui;

import com.erikmafo.little.table.viewer.MainApp;
import com.erikmafo.little.table.viewer.config.AppConfig;
import com.erikmafo.little.table.viewer.ui.projectexplorer.AddInstanceDialog;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
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
        FxAssert.verifyThat(lookup(".dialog-pane").queryAs(AddInstanceDialog.class), isVisible());
    }
}
