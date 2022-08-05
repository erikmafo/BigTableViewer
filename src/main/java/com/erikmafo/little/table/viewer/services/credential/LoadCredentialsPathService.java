package com.erikmafo.little.table.viewer.services.credential;

import com.erikmafo.little.table.viewer.services.internal.CredentialsManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.nio.file.Path;

public class LoadCredentialsPathService extends Service<Path> {

    private final CredentialsManager credentialsManager;

    @Inject
    public LoadCredentialsPathService(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    @Override
    protected Task<Path> createTask() {
        return new Task<Path>() {
            @Override
            protected Path call() throws Exception {
                return credentialsManager.getCredentialsPath();
            }
        };
    }
}
