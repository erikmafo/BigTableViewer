package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.ByteStringConverter;
import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.Value;
import com.erikmafo.btviewer.util.ByteStringConverterUtil;
import com.google.protobuf.ByteString;

import java.util.Collections;
import java.util.List;

public class ByteStringConverterImpl implements ByteStringConverter {
    private final List<CellDefinition> cellDefinitions;

    public ByteStringConverterImpl(BigtableTableSettings config) {
        this(config.getCellDefinitions() != null ? config.getCellDefinitions() : Collections.emptyList());
    }

    public ByteStringConverterImpl(List<CellDefinition> cellDefinitions) {
        this.cellDefinitions = cellDefinitions;
    }

    @Override
    public ByteString toByteString(Field field, Value value) {
        var valueType = cellDefinitions.stream()
                .filter(c -> c.getFamily().equals(field.getFamily()))
                .filter(c -> c.getQualifier().equals(field.getQualifier()))
                .map(CellDefinition::getValueType)
                .findFirst()
                .orElse("STRING");

        ByteString byteString;
        switch (valueType.toUpperCase()) {
            case "STRING":
                byteString = ByteStringConverterUtil.toByteString(value.asString());
                break;
            case "DOUBLE":
                byteString = ByteStringConverterUtil.toByteString(value.asDouble());
                break;
            case "FLOAT":
                byteString = ByteStringConverterUtil.toByteString(value.asFloat());
                break;
            case "INTEGER":
                byteString = ByteStringConverterUtil.toByteString(value.asInt());
                break;
            default: throw new AssertionError("Value type " + valueType.toUpperCase() + " is not supported");
        }

        return byteString;
    }
}
