package com.erikmafo.ltviewer.util;

import org.jetbrains.annotations.NotNull;

import java.util.HexFormat;

public class HexUtil {

    public static String toHex(byte[] bytes) {
        return getHexFormat().formatHex(bytes);
    }

    public static byte[] toBytes(String hex) {
        return getHexFormat().parseHex(hex);
    }

    @NotNull
    private static HexFormat getHexFormat() {
        return HexFormat.of().withUpperCase();
    }
}
