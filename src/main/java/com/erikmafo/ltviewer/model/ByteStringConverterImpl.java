package com.erikmafo.ltviewer.model;

import com.erikmafo.ltviewer.sql.ByteStringConverter;
import com.erikmafo.ltviewer.sql.Field;
import com.erikmafo.ltviewer.sql.Value;
import com.erikmafo.ltviewer.util.ByteStringConverterUtil;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ByteStringConverterImpl implements ByteStringConverter {
    private final List<CellDefinition> cellDefinitions;

    public ByteStringConverterImpl(@NotNull BigtableTableSettings config) {
        this(config.getCellDefinitions() != null ? config.getCellDefinitions() : Collections.emptyList());
    }

    public ByteStringConverterImpl(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    @Override
    public ByteString toByteString(Field field, Value value) {
        var valueType = cellDefinitions
                .stream()
                .filter(c -> c.getFamily().equals(field.getFamily()))
                .filter(c -> c.getQualifier().equals(field.getQualifier()))
                .map(CellDefinition::getValueType)
                .findFirst()
                .orElse(ValueTypeConstants.STRING);

        ByteString byteString;
        switch (valueType.toUpperCase()) {
            case ValueTypeConstants.STRING:
            case ValueTypeConstants.JSON:
                byteString = ByteStringConverterUtil.toByteString(value.asString());
                break;
            case ValueTypeConstants.DOUBLE:
                byteString = ByteStringConverterUtil.toByteString(value.asDouble());
                break;
            case ValueTypeConstants.FLOAT:
                byteString = ByteStringConverterUtil.toByteString(value.asFloat());
                break;
            case ValueTypeConstants.INTEGER:
                byteString = ByteStringConverterUtil.toByteString(value.asInt());
                break;
            case ValueTypeConstants.SHORT:
                byteString = ByteStringConverterUtil.toByteString(value.asShort());
                break;
            case ValueTypeConstants.LONG:
                byteString = ByteStringConverterUtil.toByteString(value.asLong());
                break;
            case ValueTypeConstants.BYTE_STRING:
                byteString = ByteStringConverterUtil.toByteStringFromHex(value.asString());
                break;
            default: throw new IllegalArgumentException(String.format("Value type %s is not supported", valueType.toUpperCase()));
        }

        return byteString;
    }
}
