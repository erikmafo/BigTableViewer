package com.erikmafo.btviewer.services.internal;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class DynamicCredentialsProvider implements CredentialsProvider {

    private final CredentialsManager credentialsManager;

    private Path path;
    private Credentials credentials;

    @Inject
    public DynamicCredentialsProvider(CredentialsManager credentialsManager) {
        this.credentialsManager = credentialsManager;
    }

    @Override
    public Credentials getCredentials() throws IOException {

        var newPath = credentialsManager.getCredentialsPath();

        if (path == null || !path.equals(newPath)) {
            path = newPath;
            credentials = ServiceAccountCredentials.fromStream(new FileInputStream(path.toFile()));
        }

        return credentials;
    }
}
