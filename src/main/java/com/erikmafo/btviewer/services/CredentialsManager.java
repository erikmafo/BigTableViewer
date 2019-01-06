package com.erikmafo.btviewer.services;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

/**
 * Created by erikmafo on 25.12.17.
 */
public class CredentialsManager {

    private static final String PREFERENCES_USER_ROOT_NODE_NAME = "bigtable-viewer-configs";
    private static final String CREDENTIALS_PATH = "credentials-path";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Object mutex = new Object();

    private Path credentialsPath;

    public CredentialsManager() {
        String pathAsString = getPreferences().get(CREDENTIALS_PATH, null);

        if (pathAsString != null) {
            credentialsPath = Path.of(pathAsString);
        }
    }

    public void setCredentialsPath(Path credentialsPath) {

        if (credentialsPath == null) {
            return;
        }

        synchronized (mutex) {
            if (credentialsPath.equals(this.credentialsPath)) {
                return;
            }
            this.credentialsPath = credentialsPath;
            executorService.submit(() -> getPreferences().put(CREDENTIALS_PATH, credentialsPath.toString()));
        }
    }

    public Path getCredentialsPath() {

        synchronized (mutex) {
            if (credentialsPath != null) {
                return credentialsPath;
            } else {
                return null;
            }
        }
    }

    private Preferences getPreferences() {
        return Preferences.userRoot().node(PREFERENCES_USER_ROOT_NODE_NAME);
    }
}
