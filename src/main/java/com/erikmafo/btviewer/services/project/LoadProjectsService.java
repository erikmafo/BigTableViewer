package com.erikmafo.btviewer.services.project;

import com.erikmafo.btviewer.services.internal.AppDataStorage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.List;

public class LoadProjectsService extends Service<List<String>> {

    private final AppDataStorage appDataStorage;

    @Inject
    public LoadProjectsService(AppDataStorage appDataStorage) {
        this.appDataStorage = appDataStorage;
    }

    @Override
    protected Task<List<String>> createTask() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return appDataStorage.getProjects();
            }
        };
    }
}
