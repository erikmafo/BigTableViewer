package com.erikmafo.btviewer.ui.dialogs.tablesettings;

import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableTableSettings;
import com.erikmafo.btviewer.model.CellDefinition;
import com.erikmafo.btviewer.model.ProtoObjectDefinition;
import com.erikmafo.btviewer.model.SortUtil;
import com.erikmafo.btviewer.model.ValueTypeConstants;
import com.erikmafo.btviewer.ui.shared.DialogController;
import com.erikmafo.btviewer.ui.util.AlertUtil;
import com.erikmafo.btviewer.ui.util.DialogLoaderUtil;
import com.erikmafo.btviewer.ui.dialogs.protoobject.ProtoObjectDialogController;
import com.erikmafo.btviewer.ui.util.FontAwesomeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableSettingsDialogController implements DialogController<BigtableTableSettings> {

    public static final String FXML = "fxml/table_settings_dialog.fxml";

    public static final int CHOICE_BOX_PREF_WIDTH = 85;
    private final List<ObservableCell> observableCells = new ArrayList<>();

    @FXML
    private GridPane schemaGridPane;

    private int currentSchemaRow = 1;

    @Override
    public void setInitialValue(@NotNull BigtableTableSettings settings) {
        var cellDefSorted = settings
                .getCellDefinitions()
                .stream()
                .sorted(SortUtil::byFamilyThenQualifier)
                .collect(Collectors.toList());

        for (var cellDefinition : cellDefSorted) {
            addCellDefinition(cellDefinition);
        }
    }

    @Override
    public BigtableTableSettings getResult() {
        return getBigtableTableSettings();
    }

    public void addColumns(@NotNull List<BigtableColumn> columns) {

        var columnsSorted = columns
                .stream()
                .sorted(SortUtil::byFamilyThenQualifier)
                .collect(Collectors.toList());

        for (var column : columnsSorted) {
            addDefaultCellDefinition(column);
        }

        if (observableCells.isEmpty()) {
            addEmptyCellDefinition();
        }
    }

    @FXML
    public void onAddTableRow(ActionEvent event) {
        addEmptyCellDefinition();
    }

    @NotNull
    private BigtableTableSettings getBigtableTableSettings() {
        return new BigtableTableSettings(getCellDefinitions());
    }

    @NotNull
    private List<CellDefinition> getCellDefinitions() {
        return observableCells
                .stream()
                .map(this::toCellDefinition)
                .filter(cellDefinition -> cellDefinition.isValid())
                .collect(Collectors.toList());
    }

    @NotNull
    @Contract("_ -> new")
    private CellDefinition toCellDefinition(@NotNull ObservableCell cell) {
        return new CellDefinition(cell.getValueType(), cell.getFamily(), cell.getQualifier(), cell.getProtoObjectDefinition());
    }

    private void addEmptyCellDefinition() {
        addDefaultCellDefinition(new BigtableColumn("", ""));
    }

    private void addDefaultCellDefinition(@NotNull BigtableColumn column) {
        if (!isAlreadyAddedMatching(column)) {
            addCellDefinition(new CellDefinition(
                    "ByteString", column.getFamily(), column.getQualifier(), null));
        }
    }

    private void addCellDefinition(@NotNull CellDefinition cellDefinition) {

        if (!cellDefinition.isValid() || isAlreadyAdded(cellDefinition)) {
            return;
        }

        var observableCell = new ObservableCell();

        var familyTextField = new TextField(cellDefinition.getFamily());
        observableCell.familyProperty().bind(familyTextField.textProperty());

        var qualifierTextField = new TextField(cellDefinition.getQualifier());
        qualifierTextField
                .textProperty()
                .addListener((observable, oldValue, newValue) ->
                        validateQualifierPattern(qualifierTextField, oldValue, newValue));
        observableCell.qualifierProperty().bind(qualifierTextField.textProperty());

        ChoiceBox<String> choiceBox = getValueTypeChoiceBox(cellDefinition.getValueType());
        choiceBox.setOnAction(ae -> {
            if (choiceBox.getValue().equalsIgnoreCase(ValueTypeConstants.PROTO)) {
                openProtoObjectDialog(cellDefinition.getProtoObjectDefinition(), observableCell);
            }
        });
        observableCell.valueTypeProperty().bind(choiceBox.valueProperty());

        Button configureProtoObjectButton = getConfigureProtoObjectButton(
                cellDefinition.getProtoObjectDefinition(), observableCell);
        configureProtoObjectButton.disableProperty().bind(choiceBox.valueProperty().isNotEqualTo("Proto"));
        observableCell.setProtoObjectDefinition(cellDefinition.getProtoObjectDefinition());

        Button removeSchemaRowButton = new Button(null, FontAwesomeUtil.create(FontAwesome.Glyph.TRASH));
        removeSchemaRowButton.getStyleClass().add("btn-danger");

        var hbox = new HBox();
        hbox.setSpacing(2);
        hbox.getChildren().addAll(choiceBox, configureProtoObjectButton, removeSchemaRowButton);
        var children = new Node[] { familyTextField, qualifierTextField, hbox };

        addGridPaneRow(observableCell, children);
        removeSchemaRowButton.setOnAction(ae -> removeGridPaneRow(observableCell, children));
    }

    private void removeGridPaneRow(ObservableCell observableCell, Node[] children) {
        schemaGridPane.getChildren().removeAll(children);
        observableCells.remove(observableCell);
        currentSchemaRow--;
    }

    private void addGridPaneRow(ObservableCell observableCell, Node[] children) {
        schemaGridPane.addRow(currentSchemaRow, children);
        observableCells.add(observableCell);
        currentSchemaRow++;
    }

    private static void validateQualifierPattern(TextField qualifierTextField, String oldValue, String newValue) {
        try {
            Pattern.compile(newValue);
        } catch (Exception ex) {
            qualifierTextField.setText(oldValue);
            AlertUtil.displayError("Invalid qualifier pattern '" + newValue + "'", ex);
        }
    }

    private boolean isAlreadyAdded(@NotNull CellDefinition cellDefinition) {
        return observableCells
                .stream()
                .map(ObservableCell::toCellDefinition)
                .anyMatch(cd -> cd.equals(cellDefinition));
    }

    private boolean isAlreadyAddedMatching(@NotNull BigtableColumn column) {
        return observableCells
                .stream()
                .map(ObservableCell::toCellDefinition)
                .anyMatch(cd -> cd.matches(column));
    }

    @NotNull
    private static ChoiceBox<String> getValueTypeChoiceBox(@Nullable String valueType) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        choiceBox.setValue(valueType != null ? valueType : "ByteString");
        choiceBox.getItems().setAll(Arrays.asList(
                "String", "Double", "Float", "Integer", "Long", "Short", "Json", "Proto", "ByteString", "UUID"));
        choiceBox.setPrefWidth(CHOICE_BOX_PREF_WIDTH);
        return choiceBox;
    }

    @NotNull
    private static Button getConfigureProtoObjectButton(ProtoObjectDefinition protoObjectDefinition, ObservableCell observableCell) {
        var configureProtoObjectButton = new Button("", FontAwesomeUtil.create(FontAwesome.Glyph.COG));
        configureProtoObjectButton
                .setOnAction(event -> openProtoObjectDialog(protoObjectDefinition, observableCell));

        return configureProtoObjectButton;
    }

    private static CompletableFuture<ProtoObjectDefinition> openProtoObjectDialog(ProtoObjectDefinition protoObjectDefinition, @NotNull ObservableCell observableCell) {
        return DialogLoaderUtil
                .displayDialogAndAwaitResult(
                        observableCell.getProtoObjectDefinition() != null
                                ? observableCell.getProtoObjectDefinition()
                                : protoObjectDefinition,
                        ProtoObjectDialogController.FXML)
                .whenComplete((protoObjectDef, throwable) ->
                        observableCell.setProtoObjectDefinition(protoObjectDef));
    }
}
