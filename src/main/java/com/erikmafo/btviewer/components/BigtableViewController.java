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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BigtableViewController {

    private static final String ROW_KEY = "key";

    @FXML
    private CheckBox timestampsCheckBox;

    @FXML
    private VBox vBox;

    @FXML
    private Button tableSettingsButton;

    @FXML
    private TreeTableView<BigtableRow> tableView;

    private final BigtableRowTreeItem root;

    private final SimpleObjectProperty<BigtableTable> table = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<BigtableTableSettings> tableSettings = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<BigtableValueConverter> valueConverter = new SimpleObjectProperty<>();
    private final BooleanProperty displayTimestamps = new SimpleBooleanProperty(false);

    private final SaveTableSettingsService saveTableSettingsService;
    private final LoadTableSettingsService loadTableSettingsService;

    @Inject
    public BigtableViewController(
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
        tableView.getColumns().add(createRowKeyColumn());
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

    @NotNull
    private ContextMenu createTableViewContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAutoHide(true);
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(actionEvent -> copySelectedCellsToClipboard());
        contextMenu.getItems().add(copy);
        return contextMenu;
    }

    public void setRows(ObservableList<BigtableRow> rows) {
        rows.addListener(this::onBigtableRowsChange);
    }

    private void onBigtableRowsChange(ListChangeListener.Change<? extends BigtableRow> change) {
        tableView.getColumns().clear();
        var rowKeyColumn = createRowKeyColumn();
        tableView.getColumns().add(rowKeyColumn);
        while (change.next()) {
            change.getAddedSubList()
                    .stream()
                    .flatMap(r -> r.getCells().stream())
                    .forEach(cell -> addColumn(cell.getFamily(), cell.getQualifier()));
        }
        var treeItems = change.getList()
                .stream()
                .map(BigtableRowTreeItem::new)
                .collect(Collectors.toList());
        root.getChildren().setAll(treeItems);

        tableView.resizeColumn(rowKeyColumn, 20);
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

    private String getCellValue(TreeTablePosition<BigtableRow, ?> position) {
        var row = (BigtableRow)position.getTreeItem().getValue();
        if (position.getColumn() == 0) {
            return row.getRowKey();
        }

        var family = position.getTableColumn().getParentColumn().getText();
        var qualifier = position.getTableColumn().getText();

        return row.getCellValue(family, qualifier, valueConverter.get()).toString();
    }

    private TreeTableColumn<BigtableRow, String> createRowKeyColumn() {
        TreeTableColumn<BigtableRow, String> tableColumn = new TreeTableColumn<>(ROW_KEY);
        tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue().getRowKey()));
        return tableColumn;
    }

    private void addColumn(String family, String qualifier) {
        var familyColumn = getFamilyTableColumn(family);
        var qualifierColumn = getQualifierTableColumn(family, qualifier);

        if (familyColumn == null) {
            familyColumn = new TreeTableColumn<BigtableRow, String>(family);
            familyColumn.getColumns().add(qualifierColumn);
            tableView.getColumns().add(familyColumn);
        } else if (familyColumn.getColumns().stream().noneMatch(c -> c.getText().equals(qualifier))) {
            familyColumn.getColumns().add(qualifierColumn);
        }
    }

    private TreeTableColumn<BigtableRow, BigtableCell> getQualifierTableColumn(String family, String qualifier) {
        TreeTableColumn<BigtableRow, BigtableCell> qualifierColumn = new TreeTableColumn<>(qualifier);
        qualifierColumn.setCellValueFactory(new CellValueFactory(family, qualifier));
        qualifierColumn.setCellFactory(new CellFactory());
        return qualifierColumn;
    }

    private TreeTableColumn<BigtableRow, ?> getFamilyTableColumn(String family) {
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

    static class CellValueFactory implements Callback<TreeTableColumn.CellDataFeatures<BigtableRow, BigtableCell>, ObservableValue<BigtableCell>>
    {
        private final String family;
        private final String qualifier;

        CellValueFactory(String family, String qualifier) {
            this.family = family;
            this.qualifier = qualifier;
        }

        @Override
        public ObservableValue<BigtableCell> call(TreeTableColumn.CellDataFeatures<BigtableRow, BigtableCell> param) {
            return new ReadOnlyObjectWrapper<>(param.getValue().getValue().getLatestCell(family, qualifier));
        }
    }

    class CellFactory implements Callback<TreeTableColumn<BigtableRow, BigtableCell>, TreeTableCell<BigtableRow, BigtableCell>>
    {
        @Override
        public TreeTableCell<BigtableRow, BigtableCell> call(TreeTableColumn<BigtableRow, BigtableCell> column) {
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
