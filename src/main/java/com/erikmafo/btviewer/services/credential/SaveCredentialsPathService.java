package com.erikmafo.btviewer.services.credential;

import com.erikmafo.btviewer.services.internal.CredentialsManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.nio.file.Path;

public class SaveCredentialsPathService extends Service<Void> {

    private final CredentialsManager credentialsManager;

    private Path credentialsPath;

    @Inject
    public SaveCredentialsPathService(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    public void setCredentialsPath(Path credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                credentialsManager.setCredentialsPath(credentialsPath);
                return null;
            }
        };
    }
}
