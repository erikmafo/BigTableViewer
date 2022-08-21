package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.ui.util.OperatingSystemUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class AppDataUtil {

    private static final String BIGTABLE_VIEWER = "BigtableViewer";
    private static final String LIBRARY_APPLICATION_SUPPORT = "/Library/Application Support";
    private static final String USER_HOME = System.getProperty("user.home");
    public static final String APPDATA = System.getenv("APPDATA");

    @NotNull
    public static Path getStorageFolder() {
        Path appDataFolder;

        if (OperatingSystemUtil.isWindows()) {
            appDataFolder = Path.of(APPDATA, BIGTABLE_VIEWER);
        }
        else if (OperatingSystemUtil.isMac()) {
            appDataFolder = Path.of(USER_HOME + LIBRARY_APPLICATION_SUPPORT, BIGTABLE_VIEWER);
        }
        else {
            appDataFolder = Path.of(USER_HOME, "." + BIGTABLE_VIEWER);
        }

        return appDataFolder;
    }
}
