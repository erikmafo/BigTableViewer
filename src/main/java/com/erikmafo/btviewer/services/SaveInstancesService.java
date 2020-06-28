package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableInstanceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SaveInstancesService extends Service<Void> {

    private final BigtableInstanceManager instanceManager;

    private List<BigtableInstance> instances;

    @Inject
    public SaveInstancesService(BigtableInstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    public void addInstance(BigtableInstance instance) {

        if (this.instances == null) {
            instances = new ArrayList<>();
        }

        if (!this.instances.contains(instance)) {
            this.instances.add(instance);
        }
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                instanceManager.setInstances(instances);
                return null;
            }
        };
    }
}
