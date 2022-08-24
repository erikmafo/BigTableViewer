package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.util.ProtoUtil;
import com.erikmafo.btviewer.util.UUIDConverterUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A class that facilitates conversion of byte string values from bigtable to clr types.
 */
public class BigtableValueConverter {

    private final List<CellDefinition> cellDefinitions;

    @NotNull
    @Contract("_ -> new")
    public static BigtableValueConverter from(BigtableTableSettings config) {
        if (config == null) {
            return new BigtableValueConverter(new LinkedList<>());
        }

        return new BigtableValueConverter(config.getCellDefinitions());
    }

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
     * Converts the {@link BigtableCell} to a {@link BigtableValue}.
     *
     * @param cell a bigtable cell.
     *
     * @return a {@link BigtableValue}.
     */
    public BigtableValue convert(BigtableCell cell) {
        if (cell == null) {
            return null;
        }

        CellDefinition cellDefinition = getCellDefinition(cell);

        try {
            return convertUsingValueType(cell, cellDefinition);
        } catch (BufferUnderflowException ex) {
            throw new IllegalArgumentException(String.format(cell.getValueAsString() + " is not a %s", cellDefinition.getValueType()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the {@link BigtableCell} to a clr type.
     *
     * @param cell a bigtable cell.
     *
     * @return a clr type.
     */
    public Object convertToObj(BigtableCell cell) {
        return getValueOrNull(convert(cell));
    }

    /**
     * Determines if the value of the {@link BigtableCell} is converted to a number
     * type when calling {@link #convertToObj(BigtableCell)}.
     *
     * @param cell a bigtable cell.
     * @return true if the cell value converts to a number, false otherwise.
     */
    public boolean isNumberCellDefinition(BigtableCell cell) {
        var cellDefinition = getCellDefinition(cell);

        switch (cellDefinition.getValueType().toUpperCase()) {
            case ValueTypeConstants.DOUBLE:
            case ValueTypeConstants.INTEGER:
            case ValueTypeConstants.FLOAT:
            case ValueTypeConstants.LONG:
            case ValueTypeConstants.SHORT:
                return true;
            default:
                return false;
        }
    }

    private Object getValueOrNull(BigtableValue value) {
        return value == null ? null : value.getValue();
    }

    @NotNull
    private CellDefinition getCellDefinition(@NotNull BigtableCell cell) {
        return CellDefinitionMatcherUtil
                .findBestMatch(cellDefinitions, new BigtableColumn(cell.getFamily(), cell.getQualifier()))
                .orElse(new CellDefinition(ValueTypeConstants.STRING, cell.getFamily(), cell.getQualifier(), null));
    }
    @NotNull
    private BigtableValue convertUsingValueType(BigtableCell cell, @NotNull CellDefinition cellDefinition) throws IOException {
        var valueTypeUpper = cellDefinition.getValueType().toUpperCase();
        switch (valueTypeUpper) {
            case ValueTypeConstants.DOUBLE:
                return toBigtableValue(ByteBuffer.wrap(cell.getBytes()).getDouble(), valueTypeUpper);
            case ValueTypeConstants.INTEGER:
                return toBigtableValue(ByteBuffer.wrap(cell.getBytes()).getInt(), valueTypeUpper);
            case ValueTypeConstants.FLOAT:
                return toBigtableValue(ByteBuffer.wrap(cell.getBytes()).getFloat(), valueTypeUpper);
            case ValueTypeConstants.LONG:
                return toBigtableValue(ByteBuffer.wrap(cell.getBytes()).getLong(), valueTypeUpper);
            case ValueTypeConstants.SHORT:
                return toBigtableValue(ByteBuffer.wrap(cell.getBytes()).getShort(), valueTypeUpper);
            case ValueTypeConstants.JSON:
                return toBigtableValue(cell.getValueAsString(), valueTypeUpper);
            case ValueTypeConstants.PROTO:
                return toBigtableValue(ProtoUtil.toJson(cell.getByteString(), cellDefinition.getProtoObjectDefinition()), valueTypeUpper);
            case ValueTypeConstants.BYTE_STRING:
                return toBigtableValue(cell.getValueAsStringBase64(), valueTypeUpper);
            case ValueTypeConstants.UUID:
                return toBigtableValue(UUIDConverterUtil.convertBytesToUUID(cell.getBytes()), valueTypeUpper);
            default:
                return toBigtableValue(cell.getValueAsString(), ValueTypeConstants.STRING);
        }
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private BigtableValue toBigtableValue(Object object, String valueType) {
        return new BigtableValue(object, valueType);
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
