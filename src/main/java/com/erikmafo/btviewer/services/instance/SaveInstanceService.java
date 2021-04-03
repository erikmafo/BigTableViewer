package com.erikmafo.btviewer.services.instance;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.AppDataStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;

public class SaveInstanceService extends Service<Void> {

    private final AppDataStorage appDataStorage;

    private BigtableInstance instance;

    @Inject
    public SaveInstanceService(AppDataStorage appDataStorage) {
        this.appDataStorage = appDataStorage;
    }

    public void setInstance(BigtableInstance instance) {
        this.instance = instance;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if (instance != null) {
                    appDataStorage.addInstance(instance);
                }
                return null;
            }
        };
    }
}
