package com.erikmafo.little.table.viewer.services.project;

import com.erikmafo.little.table.viewer.services.internal.AppDataStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;

public class RemoveProjectService extends Service<Void> {

    private final AppDataStorage storage;
    private String projectId;

    @Inject
    public RemoveProjectService(AppDataStorage storage) {
        this.storage = storage;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (projectId != null) {
                    storage.removeProject(projectId);
                }
                return null;
            }
        };
    }
}
