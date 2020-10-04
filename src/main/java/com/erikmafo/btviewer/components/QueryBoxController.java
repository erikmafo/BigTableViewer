package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.model.BigtableRow;
import com.erikmafo.btviewer.model.BigtableTable;
import com.erikmafo.btviewer.services.ReadRowsService;
import com.erikmafo.btviewer.sql.SqlParser;
import com.erikmafo.btviewer.sql.SqlQuery;
import com.erikmafo.btviewer.util.AlertUtil;
import javafx.application.Platform;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.jetbrains.annotations.Nullable;
import org.reactfx.Subscription;

import javax.inject.Inject;
import java.time.Duration;
import java.util.regex.Pattern;

import static com.erikmafo.btviewer.components.SyntaxHighlightingUtil.computeSyntaxHighlighting;

public class QueryBoxController {

    private static final Pattern WHITE_SPACE = Pattern.compile("^\\s+");

    @FXML
    private Button executeQueryButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private CodeArea codeArea;

    private Subscription codeAreaSubscription;

    private final ReadRowsService readRowsService;

    private final ObjectProperty<BigtableInstance> instance = new SimpleObjectProperty<>();
    private final ObjectProperty<BigtableTable> table = new SimpleObjectProperty<>();
    private final ObjectProperty<SqlQuery> query = new SimpleObjectProperty<>();
    private final ObservableList<BigtableRow> queryResult = FXCollections.observableArrayList();

    @Inject
    public QueryBoxController(ReadRowsService readRowsService) {
        this.readRowsService = readRowsService;
    }

    @FXML
    public void initialize() {
        progressBar.visibleProperty().bind(readRowsService.runningProperty());
        progressBar.progressProperty().bind(readRowsService.progressProperty());
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeAreaSubscription = codeArea
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(100))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeSyntaxHighlighting(codeArea.getText())));
        table.bind(Bindings.createObjectBinding(this::createTable, instance, query));
        instance.addListener(this::onInstanceChanged);
    }

    public ObjectProperty<BigtableTable> tableProperty() { return table; }

    public ObservableList<BigtableRow> getQueryResult() { return queryResult; }

    public void setQuery(String sql) {
        codeArea.clear();
        codeArea.replaceText(0, 0, sql);
    }

    public ObjectProperty<BigtableInstance> instanceProperty() {
        return instance;
    }

    @FXML
    private void onExecuteQueryButtonPressed(ActionEvent actionEvent) {
        try {
            queryResult.clear();
            query.set(new SqlParser().parse(codeArea.getText()).ensureValid());
            readRowsService.setInstance(instance.get());
            readRowsService.setQuery(query.get());
            readRowsService.setOnSucceeded(event -> queryResult.setAll(readRowsService.getValue()));
            readRowsService.setOnFailed(stateEvent -> Platform.runLater(() -> AlertUtil.displayError("Failed to execute query: ", stateEvent)));
            readRowsService.restart();
        } catch (Exception ex) {
            AlertUtil.displayError("Invalid query", ex);
        }
    }

    @FXML
    private void onKeyPressedInCodeArea(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            int caretPosition = codeArea.getCaretPosition();
            int currentParagraph = codeArea.getCurrentParagraph();
            var matcher = WHITE_SPACE.matcher(codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
            if (matcher.find()) {
                Platform.runLater(() -> codeArea.insertText(caretPosition, matcher.group()));
            }
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

    private void onInstanceChanged(ObservableValue<? extends BigtableInstance> obs, BigtableInstance prev, BigtableInstance current) {
        queryResult.clear();
    }
}
