/**
 * Parts of the code in this file is based on a sample from the
 * https://github.com/FXMisc/RichTextFX repository with the following licence:
 *
 * Copyright (c) 2013-2017, Tomas Mikula and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class QueryBoxController {

    private static final String[] KEYWORDS = new String[] {
            "SELECT", "FROM", "WHERE", "LIKE", "AND", "LIMIT", "KEY", "TIMESTAMP"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = "([\"'])*?\\1"; // (["'])(?:(?=(\\?))\2.)*?\1
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern HIGHLIGHT = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern WHITE_SPACE = Pattern.compile( "^\\s+" );

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

    @Inject
    public QueryBoxController(ReadRowsService readRowsService) {
        this.readRowsService = readRowsService;
        tableProperty().bind(Bindings.createObjectBinding(() -> {
            var instance = this.instance.get();
            var query = this.query.get();
            if (instance != null && query != null && query.getTableName() != null) {
                return new BigtableTable(instance, query.getTableName());
            }
            return null;
        }, instance, query));
    }

    @FXML
    public void initialize() {
        progressBar.visibleProperty().bind(readRowsService.runningProperty());
        progressBar.progressProperty().bind(readRowsService.progressProperty());
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.addEventHandler(KeyEvent.KEY_PRESSED, this::moveCaretToCorrectPosition);
        codeAreaSubscription = codeArea
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(100))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeSyntaxHighlighting(codeArea.getText())));

    }

    public ObjectProperty<BigtableTable> tableProperty() { return table; }

    public ReadOnlyObjectProperty<List<BigtableRow>> queryResultProperty() { return readRowsService.valueProperty(); }

    public void onExecuteQuery(ActionEvent actionEvent) {
        try {
            query.set(new SqlParser().parse(codeArea.getText()));
            readRowsService.setInstance(instance.get());
            readRowsService.setQuery(query.get());
            readRowsService.restart();
        } catch (Exception ex) {
            AlertUtil.displayError("Invalid query", ex);
        }
    }

    public void setQuery(String sql) {
        codeArea.clear();
        codeArea.replaceText(0, 0, sql);
    }

    public ObjectProperty<BigtableInstance> instanceProperty() {
        return instance;
    }

    @PreDestroy
    public void preDestroy() {
        codeAreaSubscription.unsubscribe();
    }

    private void moveCaretToCorrectPosition(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            int caretPosition = codeArea.getCaretPosition();
            int currentParagraph = codeArea.getCurrentParagraph();
            var matcher = WHITE_SPACE.matcher(codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
            if (matcher.find()) {
                Platform.runLater(() -> codeArea.insertText(caretPosition, matcher.group()));
            }
        }
    }

    private static StyleSpans<Collection<String>> computeSyntaxHighlighting(String text) {
        var matcher = HIGHLIGHT.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            var styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("STRING") != null ? "string" :
                                    matcher.group("COMMENT") != null ? "comment" : null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(),matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass),matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(),text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
