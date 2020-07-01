package com.erikmafo.btviewer.components;

import com.erikmafo.btviewer.events.ScanTableAction;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RowSelectionView extends VBox {

    @FXML
    private Button scanTableButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField prefixTextField;

    @FXML
    private TextField fromTextField;

    @FXML
    private TextField toTextField;

    public RowSelectionView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/row_selection_view.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load fxml", e);
        }
        progressBar.setVisible(false);
    }

    public void setOnScanTable(EventHandler<ScanTableAction> eventHandler) {
        scanTableButton.setOnAction(actionEvent ->
                eventHandler.handle(new ScanTableAction(
                        prefixTextField.getText(), fromTextField.getText(), toTextField.getText())));
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
