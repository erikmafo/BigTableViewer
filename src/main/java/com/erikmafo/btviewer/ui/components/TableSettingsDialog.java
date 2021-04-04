package com.erikmafo.btviewer.ui.components;

import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableSettings;
import com.erikmafo.btviewer.model.CellDefinition;
import com.erikmafo.btviewer.util.FXMLLoaderUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 24.12.17.
 */
public class TableSettingsDialog extends DialogPane {

    private final List<ObservableCell> observableCells = new ArrayList<>();

    @FXML
    private GridPane schemaGridPane;

    private int currentSchemaRow = 1;

    private TableSettingsDialog() {
        FXMLLoaderUtil.loadFxml("/fxml/table_settings_dialog.fxml", this);
    }

    @NotNull
    public static CompletableFuture<BigtableTableSettings> displayAndAwaitResult(
            @NotNull List<BigtableColumn> columns,
            BigtableTableSettings currentSettings) {

        var settings = currentSettings != null ? currentSettings : new BigtableTableSettings();
        CompletableFuture<BigtableTableSettings> future = new CompletableFuture<>();
        Dialog<BigtableTableSettings> dialog = new Dialog<>();
        TableSettingsDialog settingsDialog = new TableSettingsDialog();
        settings.getCellDefinitions().forEach(settingsDialog::addSchemaRow);
        columns.forEach(settingsDialog::addSchemaRow);
        if (settingsDialog.observableCells.isEmpty()) {
            settingsDialog.addSchemaRow();
        }

        dialog.setDialogPane(settingsDialog);
        dialog.getResult();
        dialog.setResultConverter(buttonType -> {
            if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {
                return settingsDialog.getBigtableTableConfiguration();
            }
            return null;
        });

        dialog.setOnHidden(event -> {
            BigtableTableSettings configuration = dialog.getResult();
            future.complete(configuration);
        });

        dialog.show();

        return future;
    }

    @FXML
    public void onAddTableRow(ActionEvent event) {
        addSchemaRow();
    }

    @NotNull
    private BigtableTableSettings getBigtableTableConfiguration() {
        var cellDefinitionList = observableCells
                .stream()
                .map(this::toCellDefinition)
                .collect(Collectors.toList());
        return new BigtableTableSettings(cellDefinitionList);
    }

    @NotNull
    @Contract("_ -> new")
    private CellDefinition toCellDefinition(@NotNull ObservableCell cell) {
        return new CellDefinition(cell.getValueType(), cell.getFamily(), cell.getQualifier());
    }

    private void addSchemaRow() {
        addSchemaRow(new BigtableColumn("", ""));
    }

    private void addSchemaRow(BigtableColumn column) {
        addSchemaRow(column, null);
    }

    private void addSchemaRow(@NotNull CellDefinition cellDefinition) {
        addSchemaRow(new BigtableColumn(
                cellDefinition.getFamily(), cellDefinition.getQualifier()), cellDefinition.getValueType());
    }

    private void addSchemaRow(BigtableColumn column, String valueType) {
        var alreadyAdded = observableCells.stream().anyMatch(cell ->
                cell.getFamily().equals(column.getFamily()) &&
                cell.getQualifier().equals(column.getQualifier()));

        if (alreadyAdded) {
            return;
        }

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setValue(valueType != null ? valueType : "String");
        choiceBox.getItems().setAll(Arrays.asList("String", "Double", "Float", "Integer"));
        TextField familyTextField = new TextField(column.getFamily());
        TextField qualifierTextField = new TextField(column.getQualifier());

        ObservableCell observableCell = new ObservableCell();
        observableCell.familyProperty().bind(familyTextField.textProperty());
        observableCell.qualifierProperty().bind(qualifierTextField.textProperty());
        observableCell.valueTypeProperty().bind(choiceBox.valueProperty());

        observableCells.add(observableCell);
        schemaGridPane.addRow(currentSchemaRow, familyTextField, qualifierTextField, choiceBox);
        currentSchemaRow++;
    }

    private static class ObservableCell {

        private final StringProperty valueType = new SimpleStringProperty();
        private final StringProperty family = new SimpleStringProperty();
        private final StringProperty qualifier = new SimpleStringProperty();

        public String getValueType() {
            return valueType.get();
        }

        public StringProperty valueTypeProperty() {
            return valueType;
        }

        public void setValueType(String valueType) {
            this.valueType.set(valueType);
        }

        public String getFamily() {
            return family.get();
        }

        public StringProperty familyProperty() {
            return family;
        }

        public void setFamily(String family) {
            this.family.set(family);
        }

        public String getQualifier() {
            return qualifier.get();
        }

        public StringProperty qualifierProperty() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier.set(qualifier);
        }
    }
}
