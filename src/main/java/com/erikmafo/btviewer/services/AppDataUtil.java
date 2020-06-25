package com.erikmafo.btviewer.services;

import com.sun.javafx.PlatformUtil;
import java.nio.file.Path;

public class AppDataUtil {

    private static final String BT_VIEWER = "btviewer";

    public static Path getStorageFolder() {
        Path appDataFolder;

        if (PlatformUtil.isWindows()) {
            appDataFolder = Path.of(System.getenv("APPDATA"), BT_VIEWER);
        }
        else if (PlatformUtil.isMac()) {
            appDataFolder = Path.of(
                    System.getProperty("user.home") + "/Library/Application Support", BT_VIEWER);
        }
        else {
            appDataFolder = Path.of(System.getProperty("user.home"), "." + BT_VIEWER);
        }
        return appDataFolder;
    }
}
