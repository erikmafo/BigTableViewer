package com.erikmafo.btviewer.model;

/**
 * Created by erikmafo on 23.12.17.
 */
public class BigtableRowRange {

    public static BigtableRowRange DEFAULT = new BigtableRowRange("", "~");

    private final String from;
    private final String to;

    public BigtableRowRange(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
