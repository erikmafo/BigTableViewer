package com.erikmafo.btviewer.ui.querybox;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.model.QueryResultRow;
import com.erikmafo.btviewer.services.query.BigtableQueryService;
import com.erikmafo.btviewer.sql.SqlParser;
import com.erikmafo.btviewer.sql.SqlQuery;
import com.erikmafo.btviewer.ui.timer.TimerView;
import com.erikmafo.btviewer.ui.util.AlertUtil;
import com.erikmafo.btviewer.ui.util.ContextMenuUtil;
import com.erikmafo.btviewer.util.StringUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.time.Duration;
import java.util.regex.Pattern;

import static com.erikmafo.btviewer.ui.querybox.SyntaxHighlightingUtil.computeSyntaxHighlighting;

public class QueryBoxController {

    private static final Pattern WHITE_SPACE = Pattern.compile("^\\s+");

    private final BigtableQueryService bigtableQueryService;

    @FXML
    private Button executeQueryButton;

    @FXML
    private Button cancelQueryButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private CodeArea codeArea;

    @FXML
    private TimerView timer;

    private final ObjectProperty<BigtableInstance> instance = new SimpleObjectProperty<>();
    private final ObjectProperty<BigtableTable> table = new SimpleObjectProperty<>();
    private final ObjectProperty<SqlQuery> query = new SimpleObjectProperty<>();
    private final ObservableList<QueryResultRow> queryResult = FXCollections.observableArrayList();

    @Inject
    public QueryBoxController(BigtableQueryService bigtableQueryService) {
        this.bigtableQueryService = bigtableQueryService;
    }

    @FXML
    public void initialize() {
        progressBar.visibleProperty().bind(bigtableQueryService.runningProperty());
        progressBar.progressProperty().bind(bigtableQueryService.progressProperty());
        codeArea.setOnMousePressed(this::onMousePressed);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(100))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeSyntaxHighlighting(codeArea.getText())));
        table.bind(Bindings.createObjectBinding(this::createTable, instance, query));
        instance.addListener((observable, prevInstance, newInstance) -> onInstanceChanged());
        executeQueryButton.disableProperty().bind(Bindings
                .createBooleanBinding(() ->
                        StringUtil.isNullOrEmpty(codeArea.textProperty().getValue()), codeArea.textProperty()));
        cancelQueryButton.disableProperty().bind(bigtableQueryService.runningProperty().not());
        bigtableQueryService.runningProperty().addListener((obs, wasRunning, isRunning) -> updateTimer(isRunning));
    }

    @NotNull
    public ObjectProperty<BigtableTable> tableProperty() { return table; }

    @NotNull
    public ObservableList<QueryResultRow> getQueryResult() { return queryResult; }

    public void setQuery(String sql) {
        codeArea.clear();
        codeArea.replaceText(0, 0, sql);
    }

    @NotNull
    public ObjectProperty<BigtableInstance> instanceProperty() {
        return instance;
    }

    @FXML
    public void onExecuteQueryButtonPressed(ActionEvent actionEvent) {
        try {
            queryResult.clear();
            query.set(new SqlParser().parse(codeArea.getText()).ensureValid());
            bigtableQueryService.setInstance(instance.get());
            bigtableQueryService.setQuery(query.get());
            bigtableQueryService.setOnSucceeded(event -> queryResult.setAll(bigtableQueryService.getValue()));
            bigtableQueryService.setOnFailed(stateEvent -> Platform.runLater(() -> AlertUtil.displayError("Failed to execute query: ", stateEvent)));
            bigtableQueryService.restart();
        } catch (Exception ex) {
            AlertUtil.displayError("Invalid query", ex);
        }
    }

    @FXML
    public void onCancelQueryButtonPressed(ActionEvent actionEvent) { bigtableQueryService.cancel(); }

    @FXML
    public void onKeyPressedInCodeArea(@NotNull KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            int caretPosition = codeArea.getCaretPosition();
            int currentParagraph = codeArea.getCurrentParagraph();
            var matcher = WHITE_SPACE.matcher(codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
            if (matcher.find()) {
                Platform.runLater(() -> codeArea.insertText(caretPosition, matcher.group()));
            }
        }
    }

    private void updateTimer(boolean isRunningQuery) {
        if (isRunningQuery) {
            timer.startFromZero();
        } else {
            timer.stop();
        }
    }

    @Nullable
    private BigtableTable createTable() {
        var instance = this.instance.get();
        var query = this.query.get();
        if (instance != null && query != null && query.getTableName() != null) {
            return new BigtableTable(instance, query.getTableName());
        }
        return null;
    }

    private void onInstanceChanged() { queryResult.clear(); }

    private void onMousePressed(@NotNull MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            codeArea.setContextMenu(getOrCreateContextMenu());
        } else {
            codeArea.hideContextMenu();
        }
    }

    @NotNull
    private ContextMenu getOrCreateContextMenu() {
        ContextMenu contextMenu = codeArea.getContextMenu();
        if (contextMenu == null) {
            contextMenu = ContextMenuUtil.createContextMenu(
                    ContextMenuUtil.createCutMenuItem(e -> codeArea.cut()),
                    ContextMenuUtil.createCopyMenuItem(e -> codeArea.copy()),
                    ContextMenuUtil.createPasteMenuItem(e -> codeArea.paste()));
        }
        return contextMenu;
    }
}
