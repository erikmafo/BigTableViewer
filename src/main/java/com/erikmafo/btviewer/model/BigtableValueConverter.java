package com.erikmafo.btviewer.model;

import org.jetbrains.annotations.NotNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by erikmafo on 23.12.17.
 */
public class BigtableValueConverter {

    public static BigtableValueConverter from(BigtableTableSettings config) {
        if (config == null) {
            return new BigtableValueConverter(new LinkedList<>());
        }

        return new BigtableValueConverter(config.getCellDefinitions());
    }

    private final List<CellDefinition> cellDefinitions;

    public BigtableValueConverter(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

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
    private CellDefinition getCellDefinition(BigtableCell cell) {
        var cellDefinition = cellDefinitions.stream()
                .filter(c -> c.getFamily().equals(cell.getFamily())
                        && c.getQualifier().equals(cell.getQualifier()))
                .findFirst()
                .orElse(new CellDefinition("string", cell.getFamily(), cell.getQualifier()));
        return cellDefinition;
    }

    private Object convertUsingValueType(BigtableCell cell, String valueType) {
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
