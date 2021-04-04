package com.erikmafo.btviewer.util;

public class Check {

    public static <T> void notNull(T param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null", paramName));
        }
    }

    public static <T> void notNullOrEmpty(String param, String paramName) {
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be null or empty", paramName));
        }
    }
}
