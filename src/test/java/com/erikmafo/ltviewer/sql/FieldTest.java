package com.erikmafo.ltviewer.sql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldTest {

    @Test
    public void shouldParseQualifierAndFamily() {
        var field = new Field("family.qualifier");
        assertEquals("family", field.getFamily());
        assertEquals("qualifier", field.getQualifier());
    }
}
