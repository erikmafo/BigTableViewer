package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableInstanceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.List;

public class LoadInstancesService extends Service<List<BigtableInstance>> {

    private final BigtableInstanceManager instanceManager;

    @Inject
    public LoadInstancesService(BigtableInstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    protected Task<List<BigtableInstance>> createTask() {
        return new Task<>() {
            @Override
            protected List<BigtableInstance> call() throws Exception {
                return instanceManager.getInstances();
            }
        };
    }
}
