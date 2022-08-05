package com.erikmafo.ltviewer.ui.queryresult.cell;

import com.erikmafo.ltviewer.model.BigtableCell;
import com.erikmafo.ltviewer.model.BigtableValue;
import com.erikmafo.ltviewer.model.BigtableValueConverter;
import com.erikmafo.ltviewer.model.ValueTypeConstants;
import com.erikmafo.ltviewer.util.FXMLLoaderUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class CellView extends BorderPane {

    private final ObjectProperty<BigtableCell> cell = new SimpleObjectProperty<>();

    private final BooleanProperty displayTimestamp = new SimpleBooleanProperty();

    private final ObjectProperty<CellTimestampDisplayMode> timestampDisplayMode = new SimpleObjectProperty<>();

    private final ObjectProperty<BigtableValueConverter> valueConverter = new SimpleObjectProperty<>();

    @FXML
    private VBox valuePane;

    @FXML
    private Label versionLabel;

    public CellView() {
        FXMLLoaderUtil.loadFxml("/fxml/cell_view.fxml", this);
    }

    @FXML
    public void initialize() {

        Bindings.createObjectBinding(this::getContent, cell, valueConverter)
                .addListener((observable, oldValue, newValue) -> setContent(newValue));

        versionLabel
                .textProperty()
                .bind(Bindings.createStringBinding(this::getTimestampDisplayValue, cell, timestampDisplayMode));

        versionLabel.visibleProperty().bind(displayTimestamp);
    }

    public void setBigtableCell(BigtableCell bigtableCell) {
        this.cell.set(bigtableCell);
    }

    public boolean getDisplayTimestamp() {
        return displayTimestamp.get();
    }

    @NotNull
    public BooleanProperty displayTimestampProperty() {
        return displayTimestamp;
    }

    @NotNull
    public ObjectProperty<CellTimestampDisplayMode> timestampDisplayModeProperty() {
        return timestampDisplayMode;
    }

    @NotNull
    public ObjectProperty<BigtableValueConverter> valueConverterProperty() { return valueConverter; }

    private void setContent(Node content) {
        valuePane.getChildren().clear();
        if (content != null) {
            valuePane.getChildren().setAll(content);
        }
    }

    @Nullable
    private Node getContent() {
        return CellContentFactory.getContent(getBigtableValue(cell.getValue(), valueConverter.getValue()));
    }

    private BigtableValue getBigtableValue(BigtableCell cell, BigtableValueConverter converter) {
        if (cell == null) {
            return null;
        }

        if (converter == null) {
            return new BigtableValue(cell.getValueAsString(), ValueTypeConstants.STRING);
        }

        return converter.convert(cell);
    }

    private String getTimestampDisplayValue() {
        if (cell.getValue() == null) {
            return "";
        }

        var micros = cell.get().getTimestamp();
        var format = timestampDisplayMode.getValue();

        switch (format) {
            case MICROS:
                return toString(micros);
            case MILLIS:
                return toString(TimeUnit.MILLISECONDS.convert(micros, TimeUnit.MICROSECONDS));
            case DATE_TIME:
                return getDateTimeString(micros);
            case NONE:
                return "";
            default:
                throw new IllegalArgumentException("Invalid timestamp display mode " + timestampDisplayMode.getValue());
        }
    }

    private String toString(long millis) {
        return String.format("%s", millis);
    }

    private String getDateTimeString(long micros) {
        String timestampDisplayValue;
        long millis = TimeUnit.MILLISECONDS.convert(micros, TimeUnit.MICROSECONDS);
        var instant = Instant.ofEpochMilli(millis);
        var dateTime = ZonedDateTime.ofInstant(instant, ZoneId.ofOffset("", ZoneOffset.UTC));
        timestampDisplayValue = String.format("%s", dateTime);
        return timestampDisplayValue;
    }
}
