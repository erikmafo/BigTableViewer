package com.erikmafo.ltviewer.util;

public class OperatingSystemUtil {

    private static final String OperatingSystem = System.getProperty("os.name").toLowerCase();

    public static boolean isMac() {
        return OperatingSystem.startsWith("mac");
    }

    public static boolean isWindows() {
        return OperatingSystem.startsWith("windows");
    }
}
