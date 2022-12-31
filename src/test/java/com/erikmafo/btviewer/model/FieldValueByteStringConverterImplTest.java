package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.convert.FieldValueByteStringConverter;
import com.erikmafo.btviewer.sql.query.Field;
import com.erikmafo.btviewer.sql.query.Value;
import com.erikmafo.btviewer.sql.query.ValueType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class FieldValueByteStringConverterImplTest {

    private FieldValueByteStringConverter converter = new FieldValueByteStringConverterImpl(Arrays.asList(
            new CellDefinition(ValueTypeConstants.STRING, "stringFamily", "stringColumn", null),
            new CellDefinition(ValueTypeConstants.DOUBLE, "doubleFamily", "doubleColumn", null),
            new CellDefinition(ValueTypeConstants.SHORT, "shortFamily", "shortColumn", null),
            new CellDefinition(ValueTypeConstants.LONG, "longFamily", "longColumn", null),
            new CellDefinition(ValueTypeConstants.FLOAT, "floatFamily", "floatColumn", null),
            new CellDefinition(ValueTypeConstants.INTEGER, "intFamily", "intColumn", null)));

    @Test
    public void shouldConvertStringValue() {
        //given
        var field = new Field("stringFamily.stringColumn");
        var value = new Value("stringValue", ValueType.STRING);

        //when
        var byteString = converter.convert(field, value);

        //then
        assertEquals("stringValue", byteString.toStringUtf8());
    }

    @Test
    @Parameters({
            "intFamily.intColumn, 42, 4",
            "floatFamily.floatColumn, 42, 4",
            "doubleFamily.doubleColumn, 42, 8",
            "longFamily.longColumn, 42, 8",
            "shortFamily.longColumn, 42, 2",
    })
    public void shouldConvertNumberToCorrectNoOfBytes(String fieldName, String val, int expectedSize) {
        //given
        var field = new Field(fieldName);
        var value = new Value(val, ValueType.NUMBER);

        //when
        var byteString = converter.convert(field, value);

        //then
        assertEquals(expectedSize, byteString.size());
    }
}