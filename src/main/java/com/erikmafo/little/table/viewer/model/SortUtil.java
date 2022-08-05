package com.erikmafo.little.table.viewer.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SortUtil {

    @Contract(pure = true)
    public static int byFamilyThenQualifier(@NotNull BigtableColumn o1, @NotNull BigtableColumn o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(
                concat(o1.getFamily(), o1.getQualifier()),
                concat(o2.getFamily(), o2.getQualifier()));
    }

    @Contract(pure = true)
    public static int byFamilyThenQualifier(@NotNull CellDefinition o1, @NotNull CellDefinition o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(
                concat(o1.getFamily(), o1.getQualifier()),
                concat(o2.getFamily(), o2.getQualifier()));
    }

    @Contract(pure = true)
    private static String concat(String s1, String s2) {
        return String.format("%s%s", s1, s2);
    }
}
