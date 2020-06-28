package com.erikmafo.btviewer.services.internal;

import java.nio.file.Path;
import java.util.prefs.Preferences;

/**
 * Created by erikmafo on 25.12.17.
 */
public class CredentialsManager {

    private static final String BIGTABLE_VIEWER = "BigtableViewer";
    private static final String CREDENTIALS_PATH = "credentials-path";
    private static final String GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";

    private Path credentialsPath;

    public CredentialsManager() {
        String pathAsString = getPreferences().get(CREDENTIALS_PATH, getDefaultCredentialsPath());

        if (pathAsString != null) {
            credentialsPath = Path.of(pathAsString);
        }
    }

    public void setCredentialsPath(Path credentialsPath) {

        if (credentialsPath == null) {
            return;
        }

        if (credentialsPath.equals(this.credentialsPath)) {
            return;
        }

        this.credentialsPath = credentialsPath;
        getPreferences().put(CREDENTIALS_PATH, credentialsPath.toString());
    }

    public Path getCredentialsPath() {

        if (credentialsPath != null) {
            return credentialsPath;
        } else {
            return null;
        }
    }

    private String getDefaultCredentialsPath() {
        return System.getenv(GOOGLE_APPLICATION_CREDENTIALS);
    }

    private Preferences getPreferences() {
        return Preferences.userRoot().node(BIGTABLE_VIEWER);
    }
}
