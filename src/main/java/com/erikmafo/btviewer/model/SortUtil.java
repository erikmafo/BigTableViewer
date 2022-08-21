package com.erikmafo.btviewer.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SortUtil {

    @Contract(pure = true)
    public static int byFamilyThenQualifier(@NotNull BigtableColumn o1, @NotNull BigtableColumn o2) {
        return compare(o1.getFamily(), o1.getQualifier(), o2.getFamily(), o2.getQualifier());
    }

    @Contract(pure = true)
    public static int byFamilyThenQualifier(@NotNull CellDefinition o1, @NotNull CellDefinition o2) {
        return compare(o1.getFamily(), o1.getQualifier(), o2.getFamily(), o2.getQualifier());
    }

    public static int byQualifierLengthDescending(@NotNull CellDefinition o1, @NotNull CellDefinition o2) {
        return Integer.compare(o1.getQualifier().length(), o2.getQualifier().length());
    }

    @Contract(pure = true)
    private static String concat(String s1, String s2) {
        return String.format("%s%s", s1, s2);
    }

    private static int compare(String family1, String qualifier1, String family2, String qualifier2) {
        return String.CASE_INSENSITIVE_ORDER.compare(concat(family1, qualifier1), concat(family2, qualifier2));
    }
}
