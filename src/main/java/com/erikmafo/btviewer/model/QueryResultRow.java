package com.erikmafo.btviewer.model;

import java.util.*;

/**
 * Represents a row that is returned from the query. This could be a Bigtable row or a list of aggregations.
 */
public class QueryResultRow {

    private static final int MAX_PREVIOUS_VERSIONS = 100;

    private final String rowKey;
    private final List<BigtableCell> cells;
    private final List<Aggregation> aggregations;

    /**
     * Creates a QueryResultRow from a Bigtable row.
     * @param rowKey the key of the bigtable row.
     * @param cells the cells in the row that was returned from the query.
     */
    public QueryResultRow(String rowKey, List<BigtableCell> cells) {
        this.rowKey = rowKey;
        this.cells = cells;
        this.aggregations = new ArrayList<>();
    }

    /**
     * Creates a QueryResultRow from a list of aggregation results.
     * @param aggregations aggregation results from the query.
     */
    public QueryResultRow(Aggregation... aggregations) {
        this.rowKey = null;
        this.cells = new ArrayList<>();
        this.aggregations = new ArrayList<>();
        this.aggregations.addAll(Arrays.asList(aggregations));
    }

    /**
     * Checks whether this QueryResultRow is created from a Bigtable row.
     *
     * @return true if this is created from a Bigtable row, false otherwise.
     */
    public boolean isBigtableRow() { return rowKey != null; }

    public String getRowKey() {
        return rowKey;
    }

    public List<BigtableCell> getCells() {
        return cells;
    }

    public List<Aggregation> getAggregations() { return aggregations; }

    public Aggregation getAggregation(String name) { return aggregations.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);}

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

    public List<QueryResultRow> getPreviousVersions() {
        var versions = new ArrayList<QueryResultRow>();
        var current = this;
        var previousVersion = getPreviousVersion();

        while (previousVersion != null || versions.size() >= MAX_PREVIOUS_VERSIONS) {
            versions.add(previousVersion);
            current = previousVersion;
            previousVersion = current.getPreviousVersion();
        }

        return versions;
    }

    public QueryResultRow getPreviousVersion() {
        if (cells.isEmpty()) {
            return null;
        }

        var currentVersion = cells.stream().mapToLong(BigtableCell::getTimestamp).max().getAsLong();
        var cellsCopy = new ArrayList<>(cells);
        cellsCopy.removeIf(cell -> cell.getTimestamp() == currentVersion);

        if (cellsCopy.isEmpty()) {
            return null;
        }

        return new QueryResultRow(rowKey, cellsCopy);
    }
}
