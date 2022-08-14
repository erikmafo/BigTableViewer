package com.erikmafo.btviewer.ui.dialogs;

import com.erikmafo.btviewer.model.ProtoObjectDefinition;
import com.erikmafo.btviewer.ui.ActionEventUtil;
import com.erikmafo.btviewer.util.AlertUtil;
import com.erikmafo.btviewer.util.ProtoUtil;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;

public class ProtoObjectDialogController implements DialogController<ProtoObjectDefinition> {

    public static final String FXML = "fxml/proto_object_dialog.fxml";

    @FXML
    private TextField descriptorSetFile;

    @FXML
    private ComboBox<String> protoFile;

    @FXML
    private ComboBox<String> messageType;

    @FXML
    public void initialize() {
        disableWhenEmpty(protoFile);
        disableWhenEmpty(messageType);
    }

    @FXML
    public void handleEditDescriptorSetFile(ActionEvent event) {
        var fileChooser = new FileChooser();
        var file = fileChooser.showOpenDialog(ActionEventUtil.getWindow(event));
        if (file != null)
        {
            var descriptorSetFilePath = file.getPath();
            descriptorSetFile.setText(descriptorSetFilePath);
            populateProtoFileChoiceItems(descriptorSetFilePath);
        }
    }

    @FXML
    public void handleProtoFileChoice(ActionEvent actionEvent) {
        var descriptorSetName = descriptorSetFile.getText();
        var protoFileName = protoFile.getValue();

        populateMessageTypeChoiceItems(descriptorSetName, protoFileName);
    }

    @Override
    public void setResult(@NotNull ProtoObjectDefinition protoObjectDefinition) {
        descriptorSetFile.setText(protoObjectDefinition.getDescriptorSetFile());

        populateProtoFileChoiceItems(protoObjectDefinition.getDescriptorSetFile());
        protoFile.setValue(protoObjectDefinition.getProtoFile());

        populateMessageTypeChoiceItems(protoObjectDefinition.getDescriptorSetFile(), protoObjectDefinition.getProtoFile());
        messageType.setValue(protoObjectDefinition.getMessageType());
    }

    @Override
    public ProtoObjectDefinition getResult() {
        return new ProtoObjectDefinition(descriptorSetFile.getText(), protoFile.getValue(), messageType.getValue());
    }

    @Override
    public boolean validateResult(@NotNull ProtoObjectDefinition protoObjectDefinition) {
        return FilePathValidatorUtil.validatePath(protoObjectDefinition.getDescriptorSetFile());
    }

    private void populateProtoFileChoiceItems(String descriptorSetFilePath) {
        populateChoiceItems(
                protoFile,
                () -> ProtoUtil.listProtoFiles(descriptorSetFilePath),
                MessageFormat.format("Unable to list proto files in {0}", descriptorSetFilePath));
    }

    private void populateMessageTypeChoiceItems(String descriptorSetName, String protoFileName) {
        populateChoiceItems(
                messageType,
                () -> ProtoUtil.listMessageTypes(descriptorSetName, protoFileName),
                MessageFormat.format("Unable to list message types in {0}", protoFileName));
    }

    private <T> void populateChoiceItems(@NotNull ComboBox<T> comboBox, Callable<List<T>> getValues, String errorMessage) {
        comboBox.getItems().clear();
        try {
            comboBox.getItems().setAll(getValues.call());
        } catch (Exception e) {
            AlertUtil.displayError(errorMessage, e);
        }
    }

    private <T> void disableWhenEmpty(@NotNull ComboBox<T> comboBox) {
        comboBox.disableProperty().bind(
                Bindings.createBooleanBinding(() -> comboBox.getItems().isEmpty(), comboBox.getItems()));
    }
}
