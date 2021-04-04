package com.erikmafo.btviewer.util;

public class Check {

    /**
     * Throws an {@link IllegalArgumentException} if the given param is null.
     *
     * @param param the param to null check.
     * @param paramName the name of the param (included in the exception message.)
     * @param <T> the type of the param to check.
     * @throws IllegalArgumentException if param is null.
     */
    public static <T> void notNull(T param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null", paramName));
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the given param is null or empty.
     *
     * @param param the param to null check.
     * @param paramName the name of the param (included in the exception message.)
     * @param <T> the type of the param to check.
     * @throws IllegalArgumentException if param is null or empty.
     */
    public static <T> void notNullOrEmpty(String param, String paramName) {
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be null or empty", paramName));
        }
    }
}
