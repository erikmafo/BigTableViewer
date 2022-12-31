package com.erikmafo.btviewer.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Check {

    /**
     * Throws an {@link IllegalArgumentException} if all the given params are null.
     * @param message the exception message.
     * @param params the params to check
     */
    @Contract(pure = true)
    public static void notAllNull(String message, @NotNull Object... params) {
        if (params.length == 0) {
            return;
        }

        var allNull = true;
        for (var param : params) {
            if (param != null) {
                allNull = false;
            }
        }

        if (allNull) {
            throw new IllegalArgumentException(message);
        }
    }

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
        if (StringUtil.isNullOrEmpty(param)) {
            throw new IllegalArgumentException(String.format("%s cannot be null or empty", paramName));
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the given list is null or empty.
     * @param param - a list.
     * @param paramName - name of the list.
     * @param <T> - the type of elements in the list.
     */
    public static <T> void notNullOrEmpty(List<T> param, String paramName) {
        notNull(param, paramName);
        notEmpty(param, paramName);
    }

    /**
     * Throws an {@link IllegalArgumentException} if the given list is empty.
     * @param param - a list.
     * @param paramName - name of the list.
     * @param <T> - the type of elements in the list.
     */
    public static <T> void notEmpty(List<T> param, String paramName) {
        if (param != null && param.isEmpty()) {
            throw new IllegalArgumentException(String.format("%s cannot be empty", paramName));
        }
    }
}
