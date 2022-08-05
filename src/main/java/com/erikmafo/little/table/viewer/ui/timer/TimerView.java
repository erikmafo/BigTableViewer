package com.erikmafo.little.table.viewer.ui.timer;

import com.erikmafo.little.table.viewer.util.FXMLLoaderUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import static javafx.beans.binding.Bindings.createIntegerBinding;

public class TimerView extends Pane {

    private static final double KEY_FRAME_DURATION_MILLIS = 1;
    private static final String TWO_DIGITS = "%02d";
    private static final String TREE_DIGITS = "%03d";

    private final Property<Duration> time = new SimpleObjectProperty<>(Duration.ZERO);
    private final Timeline timeline = new Timeline();

    @FXML
    private Label hoursLabel;

    @FXML
    private Label minutesLabel;

    @FXML
    private Label secondsLabel;

    @FXML
    private Label millisLabel;

    public TimerView() { FXMLLoaderUtil.loadFxml("/fxml/timer_view.fxml", this); }

    @FXML
    public void initialize() {
        timeline.setCycleCount(Animation.INDEFINITE);
        hoursLabel.textProperty().bind(createIntegerBinding(this::getHoursPart, time).asString(TWO_DIGITS));
        minutesLabel.textProperty().bind(createIntegerBinding(this::getMinutesPart, time).asString(TWO_DIGITS));
        secondsLabel.textProperty().bind(createIntegerBinding(this::getSecondsPart, time).asString(TWO_DIGITS));
        millisLabel.textProperty().bind(createIntegerBinding(this::getMillisPart, time).asString(TREE_DIGITS));
    }

    public void setTime(Duration duration) {
        time.setValue(duration);
    }

    public void startFromZero() {
        reset();
        start();
    }

    public void start() {
        timeline.getKeyFrames()
                .setAll(new KeyFrame(Duration.millis(KEY_FRAME_DURATION_MILLIS), event -> increaseTime()));
        timeline.play();
    }

    public void stop() { timeline.stop(); }

    public void reset() {
        stop();
        setTime(Duration.ZERO);
    }

    private void increaseTime() {
        time.setValue(time.getValue().add(Duration.ONE));
    }

    private int getMillisPart() {
        return toJavaDuration(time.getValue()).toMillisPart();
    }

    private int getSecondsPart() {
        return toJavaDuration(time.getValue()).toSecondsPart();
    }

    private int getMinutesPart() {
        return toJavaDuration(time.getValue()).toMinutesPart();
    }

    private int getHoursPart() {
        return toJavaDuration(time.getValue()).toHoursPart();
    }

    private java.time.Duration toJavaDuration(@NotNull Duration duration) {
        return java.time.Duration.ofMillis((long) duration.toMillis());
    }
}

