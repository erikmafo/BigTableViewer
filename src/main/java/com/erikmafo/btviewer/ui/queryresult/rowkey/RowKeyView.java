package com.erikmafo.btviewer.ui.queryresult.rowkey;

import com.erikmafo.btviewer.ui.util.FXMLLoaderUtil;
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
