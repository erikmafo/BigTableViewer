package com.erikmafo.btviewer.ui.dialogs.addinstance;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.ui.shared.DialogController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddInstanceDialogController implements DialogController<BigtableInstance> {

    public static final String FXML = "fxml/add_instance_dialog.fxml";

    @FXML
    private TextField projectIdTextField;

    @FXML
    private TextField instanceIdTextField;

    @Override
    public void setInitialValue(BigtableInstance value) {
        if (value.getProjectId() != null) {
            projectIdTextField.setText(value.getProjectId());
            projectIdTextField.setEditable(false);
            instanceIdTextField.requestFocus();;
        }

        if (value.getInstanceId() != null) {
            instanceIdTextField.setText(value.getInstanceId());
        }
    }

    @Override
    public BigtableInstance getResult() {
        return new BigtableInstance(
                projectIdTextField.getText(),
                instanceIdTextField.getText());
    }
}
