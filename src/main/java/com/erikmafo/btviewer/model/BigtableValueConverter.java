package com.erikmafo.btviewer.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A class that facilitates conversion of byte string values from bigtable to clr types.
 */
public class BigtableValueConverter {

    @NotNull
    @Contract("_ -> new")
    public static BigtableValueConverter from(BigtableTableSettings config) {
        if (config == null) {
            return new BigtableValueConverter(new LinkedList<>());
        }

        return new BigtableValueConverter(config.getCellDefinitions());
    }

    private final List<CellDefinition> cellDefinitions;

    /**
     * Creates a {@code BigtableValueConverter} from the specified {@link CellDefinition}'s.
     *
     * @param cellDefinitions definitions for how each cell should be converted.
     */
    public BigtableValueConverter(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

    /**
     * Converts the {@link BigtableCell} to a clr type.
     *
     * @param cell a bigtable cell.
     *
     * @return a clr type.
     */
    public Object convert(BigtableCell cell) {
        if (cell == null) {
            return null;
        }

        CellDefinition cellDefinition = getCellDefinition(cell);

        try {
            return convertUsingValueType(cell, cellDefinition.getValueType());
        } catch (BufferUnderflowException ex) {
            throw new IllegalArgumentException(String.format(cell.getValueAsString() + " is not a %s", cellDefinition.getValueType()));
        }
    }

    /**
     * Determines if the value of the {@link BigtableCell} is converted to a number
     * type when calling {@link #convert(BigtableCell)}.
     *
     * @param cell a bigtable cell.
     * @return true if the cell value converts to a number, false otherwise.
     */
    public boolean isNumber(BigtableCell cell) {
        var cellDefinition = getCellDefinition(cell);

        switch (cellDefinition.getValueType().toLowerCase()) {
            case "double":
            case "integer":
            case "float":
                return true;
            default:
                return false;
        }
    }

    @NotNull
    private CellDefinition getCellDefinition(@NotNull BigtableCell cell) {
        var cellDefinition = cellDefinitions.stream()
                .filter(c -> c.getFamily().equals(cell.getFamily())
                        && c.getQualifier().equals(cell.getQualifier()))
                .findFirst()
                .orElse(new CellDefinition("string", cell.getFamily(), cell.getQualifier()));
        return cellDefinition;
    }

    private Object convertUsingValueType(BigtableCell cell, @NotNull String valueType) {
        switch (valueType.toLowerCase()) {
            case "double":
                return ByteBuffer.wrap(cell.getBytes()).getDouble();
            case "integer":
                return ByteBuffer.wrap(cell.getBytes()).getInt();
            case "float":
                return ByteBuffer.wrap(cell.getBytes()).getFloat();
            default:
                return cell.getValueAsString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigtableValueConverter that = (BigtableValueConverter) o;
        return Objects.equals(cellDefinitions, that.cellDefinitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellDefinitions);
    }
}
