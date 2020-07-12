package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableInstanceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class LoadInstancesService extends Service<List<BigtableInstance>> {

    private final BigtableInstanceManager instanceManager;
    private String projectId;

    @Inject
    public LoadInstancesService(BigtableInstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    protected Task<List<BigtableInstance>> createTask() {
        return new Task<>() {
            @Override
            protected List<BigtableInstance> call() throws Exception {
                if (projectId == null) {
                    return Collections.emptyList();
                }
                return instanceManager.getInstances(projectId);
            }
        };
    }
}
