package com.erikmafo.btviewer.components;
import com.erikmafo.btviewer.FXMLLoaderUtil;
import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableTableConfiguration;
import com.erikmafo.btviewer.model.CellDefinition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 24.12.17.
 */
public class TableSettingsDialog extends DialogPane {

    @FXML
    private GridPane schemaGridPane;

    private List<ObservableCell> observableCells = new ArrayList<>();

    private int currentSchemaRow = 1;

    private TableSettingsDialog() {
        FXMLLoaderUtil.loadFxml("/fxml/table_settings_dialog.fxml", this);
    }

    public void onAddTableRow(ActionEvent event) {
        addSchemaRow();
    }

    private void deleteRow(GridPane grid, final int row) {
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : grid.getChildren()) {
            // get index from child
            Integer rowIndex = GridPane.getRowIndex(child);

            // handle null values for index=0
            int r = rowIndex == null ? 0 : rowIndex;

            if (r > row) {
                // decrement rows for rows after the deleted row
                GridPane.setRowIndex(child, r - 1);
            } else if (r == row) {
                // collect matching rows for deletion
                deleteNodes.add(child);
            }
        }

        // remove nodes from row
        grid.getChildren().removeAll(deleteNodes);
    }

    private BigtableTableConfiguration getBigtableTableConfiguration() {
        var cellDefinitionList = observableCells
                .stream()
                .map(this::toCellDefinition)
                .collect(Collectors.toList());
        return new BigtableTableConfiguration(cellDefinitionList);
    }

    private CellDefinition toCellDefinition(ObservableCell cell) {
        return new CellDefinition(cell.getValueType(), cell.getFamily(), cell.getQualifier());
    }

    private void addSchemaRow() {
        addSchemaRow(new BigtableColumn("", ""));
    }

    private void addSchemaRow(BigtableColumn column) {
        addSchemaRow(column, null);
    }

    private void addSchemaRow(CellDefinition cellDefinition) {
        addSchemaRow(new BigtableColumn(
                cellDefinition.getFamily(), cellDefinition.getQualifier()), cellDefinition.getValueType());
    }

    private void addSchemaRow(BigtableColumn column, String valueType) {
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

        private StringProperty valueType = new SimpleStringProperty();
        private StringProperty family = new SimpleStringProperty();
        private StringProperty qualifier = new SimpleStringProperty();

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

    public static CompletableFuture<BigtableTableConfiguration> displayAndAwaitResult(List<BigtableColumn> columns, BigtableTableConfiguration current) {
        CompletableFuture<BigtableTableConfiguration> future = new CompletableFuture<>();

        try {
            Dialog<BigtableTableConfiguration> dialog = new Dialog<>();
            TableSettingsDialog pane = new TableSettingsDialog();

            if (columns.size() == 0) {
                pane.addSchemaRow();
            }
            else if (current != null) {
                current.getCellDefinitions().forEach(pane::addSchemaRow);
            }
            else {
                columns.forEach(pane::addSchemaRow);
            }

            dialog.setDialogPane(pane);
            dialog.getResult();
            dialog.setResultConverter(buttonType -> {
                if (ButtonBar.ButtonData.OK_DONE.equals(buttonType.getButtonData())) {
                    return pane.getBigtableTableConfiguration();
                }
                return null;
            });

            dialog.setOnHidden(event -> {
                BigtableTableConfiguration configuration = dialog.getResult();
                future.complete(configuration);
            });

            dialog.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return future;
    }
}
