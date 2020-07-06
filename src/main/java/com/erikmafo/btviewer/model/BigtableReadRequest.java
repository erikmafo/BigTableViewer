package com.erikmafo.btviewer.model;

public class BigtableReadRequest {

    private final BigtableTable table;
    private final BigtableRowRange range;
    private final String prefix;
    private final int maxRows;
    private final String sql;

    BigtableReadRequest(BigtableTable table, BigtableRowRange range, String prefix, int maxRows, String sql) {
        this.table = table;
        this.range = range;
        this.prefix = prefix;
        this.maxRows = maxRows;
        this.sql = sql;
    }

    public BigtableRowRange getRange() { return range; }

    public BigtableTable getTable() { return table; }

    public String getPrefix() { return prefix; }

    public int getMaxRows() { return maxRows; }

    public String getSql() {
        return sql;
    }
}
