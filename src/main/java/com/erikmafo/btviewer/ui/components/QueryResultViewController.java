package com.erikmafo.btviewer.ui.components;

import com.erikmafo.btviewer.model.BigtableCell;
import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.BigtableTableSettings;
import com.erikmafo.btviewer.model.BigtableValueConverter;
import com.erikmafo.btviewer.model.QueryResultRow;
import com.erikmafo.btviewer.services.table.LoadTableSettingsService;
import com.erikmafo.btviewer.services.table.SaveTableSettingsService;
import com.erikmafo.btviewer.util.AlertUtil;
import com.sun.javafx.PlatformUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QueryResultViewController {

    private static final String ROW_KEY = "key";

    @FXML
    private CheckBox timestampsCheckBox;

    @FXML
    private VBox vBox;

    @FXML
    private Button tableSettingsButton;

    @FXML
    private TreeTableView<QueryResultRow> tableView;

    private final BigtableRowTreeItem root;

    private final SimpleObjectProperty<BigtableTable> table = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<BigtableTableSettings> tableSettings = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<BigtableValueConverter> valueConverter = new SimpleObjectProperty<>();
    private final BooleanProperty displayTimestamps = new SimpleBooleanProperty(false);

    private final SaveTableSettingsService saveTableSettingsService;
    private final LoadTableSettingsService loadTableSettingsService;

    @Inject
    public QueryResultViewController(
            SaveTableSettingsService saveTableSettingsService,
            LoadTableSettingsService loadTableSettingsService) {
        this.root = new BigtableRowTreeItem(null);
        this.saveTableSettingsService = saveTableSettingsService;
        this.loadTableSettingsService = loadTableSettingsService;
        valueConverter.bind(Bindings.createObjectBinding(this::createValueConverter, tableSettings));
        valueConverter.addListener(this::onValueConverterChanged);
    }

    @FXML
    public void initialize() {
        vBox.visibleProperty().bind(tableProperty().isNotNull());
        tableView.setRoot(root);
        tableView.setShowRoot(false);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setContextMenu(createTableViewContextMenu());
        tableSettings.bind(loadTableSettingsService.valueProperty());
        tableProperty().addListener((obs, prev, current) -> {
            loadTableSettingsService.setTable(current);
            loadTableSettingsService.restart();
        });
        displayTimestamps.bind(timestampsCheckBox.selectedProperty());
    }

    @FXML
    public void handleTableSettingsButtonPressed(ActionEvent actionEvent) {
        TableSettingsDialog
                .displayAndAwaitResult(getColumns(), tableSettings.getValue())
                .whenComplete((configuration, throwable) -> updateTableConfiguration(table.get(), configuration));
    }

    @FXML
    public void onTableViewKeyPressed(KeyEvent keyEvent) {
        if (isCopyOperation(keyEvent)) {
            copySelectedCellsToClipboard();
        }
    }

    @NotNull
    private ContextMenu createTableViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(actionEvent -> copySelectedCellsToClipboard());
        contextMenu.getItems().add(copy);
        return contextMenu;
    }

    public void setRows(@NotNull ObservableList<QueryResultRow> rows) {
        rows.addListener(this::onBigtableRowsChange);
    }

    public SimpleObjectProperty<BigtableTable> tableProperty() {
        return table;
    }

    private void onBigtableRowsChange(@NotNull ListChangeListener.Change<? extends QueryResultRow> change) {
        tableView.getColumns().clear();
        while (change.next()) {
            change.getAddedSubList().forEach(this::addColumns);
        }
        var treeItems = change.getList()
                .stream()
                .map(BigtableRowTreeItem::new)
                .collect(Collectors.toList());
        root.getChildren().setAll(treeItems);
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
                .map(cells -> cells.stream().map(this::getCellValue).collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n"));

        var clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    private String getCellValue(@NotNull TreeTablePosition<QueryResultRow, ?> position) {
        var row = (QueryResultRow)position.getTreeItem().getValue();
        if (position.getColumn() == 0) {
            return row.getRowKey();
        }

        var family = position.getTableColumn().getParentColumn().getText();
        var qualifier = position.getTableColumn().getText();

        return row.getCellValue(family, qualifier, valueConverter.get()).toString();
    }

    @NotNull
    private TreeTableColumn<QueryResultRow, String> createRowKeyColumn() {
        TreeTableColumn<QueryResultRow, String> tableColumn = new TreeTableColumn<>(ROW_KEY);
        tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getRowKey()));
        tableColumn.setCellValueFactory(features -> features.getValue().getParent() == root ?
                new ReadOnlyStringWrapper(features.getValue().getValue().getRowKey()) :
                new ReadOnlyStringWrapper(""));
        tableColumn.setCellFactory(new RowKeyTableCellFactory());
        return tableColumn;
    }

    private void addColumns(@NotNull QueryResultRow row) {
        if (row.isBigtableRow()) {
            row.getCells().forEach(cell -> addBigtableRowColumn(cell.getFamily(), cell.getQualifier()));
        } else {
            row.getAggregations().forEach(aggregation -> addAggregationColumn(aggregation.getName()));
        }
    }

    private void addAggregationColumn(String name) {
        var column = getColumn(name);
        if (column == null) {
            column = new TreeTableColumn<QueryResultRow, String>(name);
            column.setCellValueFactory(c -> new ReadOnlyObjectWrapper(c.getValue().getValue().getAggregation(name)));
            tableView.getColumns().add(column);
        }
    }

    private void addBigtableRowColumn(String family, String qualifier) {
        var rowKeyColumn = getColumn(ROW_KEY);
        if (rowKeyColumn == null) {
            tableView.getColumns().add(createRowKeyColumn());
        }

        var familyColumn = getColumn(family);
        var qualifierColumn = getQualifierTableColumn(family, qualifier);

        if (familyColumn == null) {
            familyColumn = new TreeTableColumn<QueryResultRow, String>(family);
            familyColumn.getColumns().add(qualifierColumn);
            tableView.getColumns().add(familyColumn);
        } else if (familyColumn.getColumns().stream().noneMatch(c -> c.getText().equals(qualifier))) {
            familyColumn.getColumns().add(qualifierColumn);
        }
    }

    @NotNull
    private TreeTableColumn<QueryResultRow, BigtableCell> getQualifierTableColumn(String family, String qualifier) {
        TreeTableColumn<QueryResultRow, BigtableCell> qualifierColumn = new TreeTableColumn<>(qualifier);
        qualifierColumn.setCellValueFactory(new CellValueFactory(family, qualifier));
        qualifierColumn.setCellFactory(new CellFactory());
        return qualifierColumn;
    }

    private TreeTableColumn<QueryResultRow, ?> getColumn(String name) {
        return tableView.getColumns()
                .stream()
                .filter(c -> c.getText().equals(name))
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

    private static class CellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<QueryResultRow, BigtableCell>, ObservableValue<BigtableCell>>
    {
        private final String family;
        private final String qualifier;

        CellValueFactory(String family, String qualifier) {
            this.family = family;
            this.qualifier = qualifier;
        }

        @Override
        public ObservableValue<BigtableCell> call(@NotNull TreeTableColumn.CellDataFeatures<QueryResultRow, BigtableCell> param) {
            return new ReadOnlyObjectWrapper<>(param.getValue().getValue().getLatestCell(family, qualifier));
        }
    }

    private static class RowKeyTableCellFactory implements Callback<TreeTableColumn<QueryResultRow, String>, TreeTableCell<QueryResultRow, String>> {

        @Override
        public TreeTableCell<QueryResultRow, String> call(TreeTableColumn<QueryResultRow, String> column) {
            return new TreeTableCell<>() {
                @Override
                protected void updateItem(String rowKey, boolean empty) {
                    super.updateItem(rowKey, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        var rowKeyView = getGraphic() != null ? (RowKeyView) getGraphic() : new RowKeyView();
                        rowKeyView.setRowKey(rowKey);
                        setGraphic(rowKeyView);
                    }
                }
            };
        }
    }

    private class CellFactory implements Callback<TreeTableColumn<QueryResultRow, BigtableCell>, TreeTableCell<QueryResultRow, BigtableCell>>
    {
        @Override
        public TreeTableCell<QueryResultRow, BigtableCell> call(TreeTableColumn<QueryResultRow, BigtableCell> column) {
            return new TreeTableCell<>() {
                @Override
                protected void updateItem(BigtableCell item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        CellView cellView = getGraphic() != null ? (CellView) getGraphic() : new CellView();
                        cellView.setBigtableCell(item);
                        cellView.valueConverterProperty().bind(valueConverter);
                        cellView.displayTimestampProperty().bind(displayTimestamps);
                        setGraphic(cellView);
                    }
                }
            };
        }
    }

}
