package com.erikmafo.btviewer.model;

import java.util.LinkedList;
import java.util.List;

public class BigtableTableConfiguration {

    private BigtableTable table;
    private List<CellDefinition> cellDefinitions;

    public BigtableTableConfiguration() {
    }

    public BigtableTableConfiguration(BigtableTable table) {
        this.table = table;
        this.cellDefinitions = new LinkedList<>();
    }

    public BigtableTableConfiguration(BigtableTable table, List<CellDefinition> cellDefinitions) {
        this.table = table;
        this.cellDefinitions = cellDefinitions;
    }

    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

    public void setCellDefinitions(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }


    public CellDefinition getCellDefinition(String family, String qualifier) {
        return cellDefinitions.stream()
                .filter(cellDefinition -> cellDefinition.getFamily().equals(family) &&
                                cellDefinition.getQualifier().equals(qualifier))
                .findFirst()
                .orElse(new CellDefinition("String", family, qualifier));
    }

    public BigtableTable getTable() {
        return table;
    }

    public void setTable(BigtableTable table) {
        this.table = table;
    }
}
