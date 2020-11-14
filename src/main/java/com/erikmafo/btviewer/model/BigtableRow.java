package com.erikmafo.btviewer.model;

import java.util.*;

/**
 * Created by erikmafo on 12.12.17.
 */
public class BigtableRow {

    private static final int MAX_PREVIOUS_VERSIONS = 100;

    private final String rowKey;
    private final List<BigtableCell> cells;

    public BigtableRow(String rowKey, List<BigtableCell> cells) {
        this.rowKey = rowKey;
        this.cells = cells;
    }

    public String getRowKey() {
        return rowKey;
    }

    public List<BigtableCell> getCells() {
        return cells;
    }

    public BigtableCell getLatestCell(String family, String qualifier) {
        return cells.stream()
                .filter(c -> family.equals(c.getFamily()) && qualifier.equals(c.getQualifier()))
                .sorted(Comparator.comparingLong(BigtableCell::getTimestamp))
                .reduce((c1, c2) -> c2)
                .orElse(null);
    }

    public Object getCellValue(String family, String qualifier, BigtableValueConverter converter) {

        BigtableCell cell = cells
                .stream()
                .filter(c -> family.equals(c.getFamily()) && qualifier.equals(c.getQualifier()))
                .sorted(Comparator.comparingLong(BigtableCell::getTimestamp))
                .reduce((c1, c2) -> c2)
                .orElse(null);

        if (cell == null) {
            return null;
        }

        if (converter == null) {
            return cell.getValueAsString();
        }

        return converter.convert(cell);
    }

    public List<BigtableRow> getPreviousVersions() {
        var versions = new ArrayList<BigtableRow>();
        var current = this;
        var previousVersion = getPreviousVersion();

        while (previousVersion != null || versions.size() >= MAX_PREVIOUS_VERSIONS) {
            versions.add(previousVersion);
            current = previousVersion;
            previousVersion = current.getPreviousVersion();
        }

        return versions;
    }

    public BigtableRow getPreviousVersion() {
        if (cells.isEmpty()) {
            return null;
        }

        var currentVersion = cells.stream().mapToLong(BigtableCell::getTimestamp).max().getAsLong();
        var cellsCopy = new ArrayList<>(cells);
        cellsCopy.removeIf(cell -> cell.getTimestamp() == currentVersion);

        if (cellsCopy.isEmpty()) {
            return null;
        }

        return new BigtableRow(rowKey, cellsCopy);
    }
}
