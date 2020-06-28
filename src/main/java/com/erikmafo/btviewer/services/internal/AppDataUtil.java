package com.erikmafo.btviewer.services.internal;

import com.sun.javafx.PlatformUtil;
import java.nio.file.Path;

class AppDataUtil {

    private static final String BIGTABLE_VIEWER = "BigtableViewer";

    public static Path getStorageFolder() {
        Path appDataFolder;

        if (PlatformUtil.isWindows()) {
            appDataFolder = Path.of(System.getenv("APPDATA"), BIGTABLE_VIEWER);
        }
        else if (PlatformUtil.isMac()) {
            appDataFolder = Path.of(
                    System.getProperty("user.home") + "/Library/Application Support", BIGTABLE_VIEWER);
        }
        else {
            appDataFolder = Path.of(System.getProperty("user.home"), "." + BIGTABLE_VIEWER);
        }
        return appDataFolder;
    }
}
