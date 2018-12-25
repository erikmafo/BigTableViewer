package com.erikmafo.btviewer.model;

/**
 * Created by erikmafo on 23.12.17.
 */
public class BigtableRowRange {

    private final String from;
    private final String to;
    private final int maxRows;

    public BigtableRowRange(String from, String to) {
        this(from, to, 0);
    }

    public BigtableRowRange(String from, String to, int maxRows) {
        this.from = from;
        this.to = to;
        this.maxRows = maxRows;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getMaxRows() {
        return maxRows;
    }
}
