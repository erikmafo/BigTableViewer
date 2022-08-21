package com.erikmafo.btviewer.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class CellDefinitionMatcherUtil {

    @NotNull
    public static Optional<CellDefinition> findBestMatch(
            @NotNull Collection<CellDefinition> cellDefinitions,
            BigtableColumn column) {
        return cellDefinitions
                .stream()
                .filter(c -> c.matchesExact(column))
                .findFirst()
                .or(() -> cellDefinitions
                        .stream()
                        .sorted(SortUtil::byQualifierLengthDescending)
                        .filter(c -> c.matches(column))
                        .findFirst());
    }
}
