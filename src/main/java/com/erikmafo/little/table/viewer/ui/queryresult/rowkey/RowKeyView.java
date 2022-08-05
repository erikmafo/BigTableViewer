package com.erikmafo.little.table.viewer.ui.queryresult.rowkey;

import com.erikmafo.little.table.viewer.util.FXMLLoaderUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


public class RowKeyView extends Pane {

    @FXML
    private Label rowKeyLabel;

    public RowKeyView() {
        FXMLLoaderUtil.loadFxml("/fxml/row_key_view.fxml", this);
    }

    public void setRowKey(String rowKey) {
        rowKeyLabel.setText(rowKey);
    }
}
