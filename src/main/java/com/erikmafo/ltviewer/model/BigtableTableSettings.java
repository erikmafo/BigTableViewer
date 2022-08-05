package com.erikmafo.ltviewer.model;

import java.util.Collections;
import java.util.List;

/**
 * Contains information about how the columns in a bigtable table should be interpreted.
 */
public class BigtableTableSettings {

    private List<CellDefinition> cellDefinitions;

    /**
     * Default constructor.
     */
    public BigtableTableSettings() {
        cellDefinitions = Collections.emptyList();
    }

    /**
     * Creates a new instance of {@code BigtableTableSettings} from a list of {@link CellDefinition}'s.
     *
     * @param cellDefinitions a list of {@code CellDefinition}'s
     */
    public BigtableTableSettings(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    /**
     * Gets all the cell definitions for the table.
     *
     * @return list of {@link CellDefinition}'s.
     */
    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

    public void setCellDefinitions(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

}
