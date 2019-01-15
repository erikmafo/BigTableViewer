package com.erikmafo.btviewer.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

/**
 * Created by erikmafo on 23.12.17.
 */
public class BigtableValueConverter {

    private final List<CellDefinition> cellDefinitions;

    public BigtableValueConverter(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    public List<CellDefinition> getCellDefinitions() {
        return cellDefinitions;
    }

    public Object convert(BigtableCell bigtableCell) {
        CellDefinition cellDefinition = cellDefinitions.stream()
                .filter(c -> c.getFamily().equals(bigtableCell.getFamily())
                        && c.getQualifier().equals(bigtableCell.getQualifier()))
                .findFirst()
                .orElse(new CellDefinition("string", bigtableCell.getFamily(), bigtableCell.getQualifier()));

        try {
            return convertUsingValueType(bigtableCell, cellDefinition.getValueType());
        } catch (BufferUnderflowException ex) {
            return String.format("not a %s", cellDefinition.getValueType());
        }

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

}
