package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.model.*;
import com.erikmafo.btviewer.services.LoadTableSettingsService;
import com.erikmafo.btviewer.services.SaveTableSettingsService;
import com.erikmafo.btviewer.util.AlertUtil;
import com.sun.javafx.PlatformUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BigtableViewController {

    private static final String ROW_KEY = "key";

    @FXML
    private VBox vBox;

    @FXML
    private Button tableSettingsButton;

    @FXML
    private TableView<BigtableRow> tableView;

    private final SimpleObjectProperty<BigtableTable> table = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<BigtableTableSettings> tableSettings = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<BigtableValueConverter> valueConverter = new SimpleObjectProperty<>();

    private final SaveTableSettingsService saveTableSettingsService;
    private final LoadTableSettingsService loadTableSettingsService;

    @Inject
    public BigtableViewController(
            SaveTableSettingsService saveTableSettingsService,
            LoadTableSettingsService loadTableSettingsService) {
        this.saveTableSettingsService = saveTableSettingsService;
        this.loadTableSettingsService = loadTableSettingsService;
        valueConverter.bind(Bindings.createObjectBinding(this::createValueConverter, tableSettings));
        valueConverter.addListener(this::onValueConverterChanged);
    }

    @FXML
    public void initialize() {
        vBox.visibleProperty().bind(tableProperty().isNotNull());
        tableView.getColumns().add(createRowKeyColumn());
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(actionEvent -> copySelectedCellsToClipboard());
        contextMenu.getItems().add(copy);
        tableView.setContextMenu(contextMenu);
        tableSettings.bind(loadTableSettingsService.valueProperty());
        tableProperty().addListener((obs, prev, current) -> {
            loadTableSettingsService.setTable(current);
            loadTableSettingsService.restart();
        });
    }

    public void setRows(ObservableList<BigtableRow> rows) {
        rows.addListener((ListChangeListener<BigtableRow>) change -> {
            tableView.getColumns().clear();
            tableView.getColumns().add(createRowKeyColumn());
            while (change.next()) {
                change.getAddedSubList()
                        .stream()
                        .flatMap(r -> r.getCells().stream())
                        .forEach(cell -> addColumn(cell.getFamily(), cell.getQualifier()));
            }
        });
        tableView.setItems(rows);
    }

    public SimpleObjectProperty<BigtableTable> tableProperty() {
        return table;
    }

    @FXML
    private void handleTableSettingsButtonPressed(ActionEvent actionEvent) {
        TableSettingsDialog
                .displayAndAwaitResult(getColumns(), tableSettings.getValue())
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table.get(), configuration));
    }

    @FXML
    private void onTableViewKeyPressed(KeyEvent keyEvent) {
        if (isCopyOperation(keyEvent)) {
            copySelectedCellsToClipboard();
        }
    }

    private boolean isCopyOperation(KeyEvent keyEvent) {
        return PlatformUtil.isMac() ?
                keyEvent.getCode() == KeyCode.C && keyEvent.isMetaDown() :
                keyEvent.getCode() == KeyCode.C && keyEvent.isControlDown();
    }

    private void copySelectedCellsToClipboard() {
        var selectedCells = tableView.getSelectionModel().getSelectedCells();
        var content = selectedCells
                .stream()
                .collect(Collectors.groupingBy(TablePositionBase::getRow))
                .values()
                .stream()
                .map(cells -> cells
                        .stream()
                        .map(this::getCellValue)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n"));

        var clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    private String getCellValue(TablePosition position) {
        var row = tableView.getItems().get(position.getRow());
        if (position.getColumn() == 0) {
            return row.getRowKey();
        }

        var family = position.getTableColumn().getParentColumn().getText();
        var qualifier = position.getTableColumn().getText();

        return row.getCellValue(family, qualifier, valueConverter.get()).toString();
    }

    private TableColumn<BigtableRow, ?> createRowKeyColumn() {
        TableColumn<BigtableRow, Object> tableColumn = new TableColumn<>(ROW_KEY);
        tableColumn.setCellValueFactory(param -> {
            var bigtableRow = param.getValue();
            return new ReadOnlyObjectWrapper<>(bigtableRow.getRowKey());
        });
        return tableColumn;
    }

    private void addColumn(String family, String qualifier) {
        var familyColumn = getFamilyTableColumn(family);
        var qualifierColumn = getQualifierTableColumn(family, qualifier);

        if (familyColumn == null) {
            familyColumn = new TableColumn<>(family);
            familyColumn.getColumns().add(qualifierColumn);
            tableView.getColumns().add(familyColumn);
        } else if (familyColumn.getColumns().stream().noneMatch(c -> c.getText().equals(qualifier))) {
            familyColumn.getColumns().add(qualifierColumn);
        }
    }

    private TableColumn<BigtableRow, Object> getQualifierTableColumn(String family, String qualifier) {
        TableColumn<BigtableRow, Object> qualifierColumn = new TableColumn<>(qualifier);
        qualifierColumn.setCellValueFactory(param -> {
            var cell = param.getValue().getLatestCell(family, qualifier);
            return new ReadOnlyObjectWrapper<>(valueConverter.get().convert(cell));
        });
        return qualifierColumn;
    }

    private TableColumn<BigtableRow, ?> getFamilyTableColumn(String family) {
        return tableView.getColumns()
                .stream()
                .filter(c -> c.getText().equals(family))
                .findFirst()
                .orElse(null);
    }

    private void updateTableConfiguration(BigtableTable table, BigtableTableSettings configuration) {
        if (configuration == null) {
            return;
        }
        saveTableConfiguration(table, configuration);
        loadTableSettingsService.restart();
    }

    private void saveTableConfiguration(BigtableTable table, BigtableTableSettings configuration) {
        saveTableSettingsService.setTableConfiguration(table, configuration);
        saveTableSettingsService.setOnFailed(event -> AlertUtil.displayError("Failed to save table configuration", event));
        saveTableSettingsService.restart();
    }

    private List<BigtableColumn> getColumns() {
        return tableView.getColumns()
                .stream()
                .filter(c -> !c.getText().equals(ROW_KEY))
                .flatMap(c -> c
                        .getColumns()
                        .stream()
                        .map(q -> new BigtableColumn(c.getText(), q.getText())))
                .collect(Collectors.toList());
    }

    private void onValueConverterChanged(ObservableValue<? extends BigtableValueConverter> obs, BigtableValueConverter prev, BigtableValueConverter current) {
        tableView.refresh();
    }

    @NotNull
    private BigtableValueConverter createValueConverter() {
        var settings = tableSettings.getValue();
        return settings != null ?
                new BigtableValueConverter(settings.getCellDefinitions()) :
                new BigtableValueConverter(Collections.emptyList());
    }
}
