package com.erikmafo.btviewer.model;

import java.util.*;

/**
 * Created by erikmafo on 12.12.17.
 */
public class BigtableRow {

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

    public Object getCellValue(String family, String qualifier, BigtableValueConverter converter) {

        BigtableCell cell = cells
                .stream()
                .filter(c -> family.equals(c.getFamily()) && qualifier.equals(c.getQualifier()))
                .findFirst()
                .orElse(null);

        if (cell == null) {
            return null;
        }

        if (converter == null) {
            return cell.getValueAsString();
        }

        return converter.convert(cell);
    }

}
