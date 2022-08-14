package com.erikmafo.btviewer.ui.timer;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class TimerViewTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new TimerView(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void shouldCreate() {
        verifyThat("#hoursLabel", isVisible());
        verifyThat("#minutesLabel", isVisible());
        verifyThat("#secondsLabel", isVisible());
        verifyThat("#millisLabel", isVisible());
    }
}