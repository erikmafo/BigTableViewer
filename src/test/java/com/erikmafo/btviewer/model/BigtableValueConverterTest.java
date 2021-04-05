package com.erikmafo.btviewer.model;

import com.google.protobuf.ByteString;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BigtableValueConverterTest {

    private BigtableValueConverter converter = new BigtableValueConverter(Arrays.asList(
            new CellDefinition(ValueTypeConstants.STRING, "stringFamily", "stringColumn"),
            new CellDefinition(ValueTypeConstants.DOUBLE, "doubleFamily", "doubleColumn"),
            new CellDefinition(ValueTypeConstants.FLOAT, "floatFamily", "floatColumn"),
            new CellDefinition(ValueTypeConstants.INTEGER, "intFamily", "intColumn")));

    @Test
    public void shouldConvertBigtableCellToInt() {
        //given
        byte[] bytes = new byte[] { 0, 0, 0, 42};
        long timestamp = ZonedDateTime.now().toEpochSecond();
        var cell = new BigtableCell("intFamily", "intColumn", ByteString.copyFrom(bytes), timestamp);

        //then
        assertTrue(converter.isNumberCellDefinition(cell));
        assertEquals(42, converter.convert(cell));
    }

    @Test
    public void shouldConvertBigtableCellToString() {
        //given
        long timestamp = ZonedDateTime.now().toEpochSecond();
        var cell = new BigtableCell("stringFamily", "stringColumn", ByteString.copyFromUtf8("42"), timestamp);

        //then
        assertFalse(converter.isNumberCellDefinition(cell));
        assertEquals("42", converter.convert(cell));
    }
}