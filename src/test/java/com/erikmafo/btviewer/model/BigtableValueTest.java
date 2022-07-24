package com.erikmafo.btviewer.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class BigtableValueTest {

    @Test
    public void asStringWithNumber_returnsQuotedNumberValue() {
        // given
        var num = 42.3;

        // when
        var value = new BigtableValue(num, ValueTypeConstants.DOUBLE);

        // then
        assertEquals("42.3", value.asString());
    }
}