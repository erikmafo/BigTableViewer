package com.erikmafo.btviewer.ui.shared;

import com.erikmafo.btviewer.model.BigtableColumn;
import com.erikmafo.btviewer.model.BigtableTableSettings;
import com.erikmafo.btviewer.model.ProtoObjectDefinition;
import com.erikmafo.btviewer.ui.dialogs.credentials.CredentialsPathDialogController;
import com.erikmafo.btviewer.ui.dialogs.protoobject.ProtoObjectDialogController;
import com.erikmafo.btviewer.ui.dialogs.tablesettings.TableSettingsDialogController;
import com.erikmafo.btviewer.ui.util.DialogLoaderUtil;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DialogService {

    public CompletableFuture<ProtoObjectDefinition> openProtoObjectDialog(ProtoObjectDefinition initialValue) {
        return DialogLoaderUtil.displayDialogAndAwaitResult(initialValue, ProtoObjectDialogController.FXML);
    }

    public CompletableFuture<Path> openCredentialsPathDialog(Path initialValue) {
        return DialogLoaderUtil.displayDialogAndAwaitResult(initialValue, CredentialsPathDialogController.FXML);
    }

    public CompletableFuture<BigtableTableSettings> openTableSettingsDialog(BigtableTableSettings initialValue, List<BigtableColumn> columns) {
        return DialogLoaderUtil.displayDialogAndAwaitResult(
                initialValue,
                TableSettingsDialogController.FXML,
                (Consumer<TableSettingsDialogController>) controller -> controller.addColumns(columns));
    }
}
