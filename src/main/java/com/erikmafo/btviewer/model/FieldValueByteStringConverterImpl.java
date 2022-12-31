package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.convert.FieldValueByteStringConverter;
import com.erikmafo.btviewer.sql.query.Field;
import com.erikmafo.btviewer.sql.query.Value;
import com.erikmafo.btviewer.util.ByteStringConverterUtil;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FieldValueByteStringConverterImpl implements FieldValueByteStringConverter {
    private final List<CellDefinition> cellDefinitions;

    public FieldValueByteStringConverterImpl(@NotNull BigtableTableSettings config) {
        this(config.getCellDefinitions() != null ? config.getCellDefinitions() : Collections.emptyList());
    }

    public FieldValueByteStringConverterImpl(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    @Override
    public ByteString convert(@NotNull Field field, Value value) {
        var valueType = CellDefinitionMatcherUtil
                .findBestMatch(cellDefinitions, new BigtableColumn(field.getFamily(), field.getQualifier()))
                .map(CellDefinition::getValueType)
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
                byteString = ByteStringConverterUtil.toByteStringFromBase64(value.asString());
                break;
            case ValueTypeConstants.UUID:
                byteString = ByteStringConverterUtil.toByteString(UUID.fromString(value.asString()));
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Value type %s is not supported", valueType.toUpperCase()));
        }

        return byteString;
    }
}
