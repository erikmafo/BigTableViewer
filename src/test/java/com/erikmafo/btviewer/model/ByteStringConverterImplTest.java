package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.ByteStringConverter;
import com.erikmafo.btviewer.sql.Field;
import com.erikmafo.btviewer.sql.Value;
import com.erikmafo.btviewer.sql.ValueType;
import com.erikmafo.btviewer.util.ByteStringConverterUtil;
import javafx.util.converter.NumberStringConverter;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class ByteStringConverterImplTest {

    private ByteStringConverter converter = new ByteStringConverterImpl(Arrays.asList(
            new CellDefinition(ValueTypes.STRING, "stringFamily", "stringColumn"),
            new CellDefinition(ValueTypes.DOUBLE, "doubleFamily", "doubleColumn"),
            new CellDefinition(ValueTypes.FLOAT, "floatFamily", "floatColumn"),
            new CellDefinition(ValueTypes.INTEGER, "intFamily", "intColumn")));

    @Test
    public void shouldConvertStringValue() {
        //given
        var field = new Field("stringFamily.stringColumn");
        var value = new Value("stringValue", ValueType.STRING);

        //when
        var byteString = converter.toByteString(field, value);

        //then
        assertEquals("stringValue", byteString.toStringUtf8());
    }

    @Test
    @Parameters({
            "intFamily.intColumn, 42, 4",
            "floatFamily.floatColumn, 42, 4",
            "doubleFamily.doubleColumn, 42, 8",
    })
    public void shouldConvertNumberToCorrectNoOfBytes(String fieldName, String val, int expectedSize) {
        //given
        var field = new Field(fieldName);
        var value = new Value(val, ValueType.NUMBER);

        //when
        var byteString = converter.toByteString(field, value);

        //then
        assertEquals(expectedSize, byteString.size());
    }
}