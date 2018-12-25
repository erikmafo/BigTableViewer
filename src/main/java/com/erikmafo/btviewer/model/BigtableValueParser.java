package com.erikmafo.btviewer.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by erikmafo on 23.12.17.
 */
public class BigtableValueParser {

    private List<CellDefinition> cellDefinitions;

    public BigtableValueParser(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    public BigtableValueParser() {
    }

    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

    public void setCellDefinitions(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    public Object parseValue(BigtableCell bigtableCell) {

        return bigtableCell.getValueAsString();
    }
}
