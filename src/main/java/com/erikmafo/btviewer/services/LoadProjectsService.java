package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableInstanceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoadProjectsService extends Service<List<String>> {

    private final BigtableInstanceManager instanceManager;

    @Inject
    public LoadProjectsService(BigtableInstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

    @Override
    protected Task<List<String>> createTask() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return Arrays.asList("project-0");
            }
        };
    }
}
