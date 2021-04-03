package com.erikmafo.btviewer.services.instance;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.AppDataStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

public class LoadInstancesService extends Service<List<BigtableInstance>> {

    private final AppDataStorage appDataStorage;
    private String projectId;

    @Inject
    public LoadInstancesService(AppDataStorage appDataStorage) {
        this.appDataStorage = appDataStorage;
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
                return appDataStorage.getInstances(projectId);
            }
        };
    }
}
