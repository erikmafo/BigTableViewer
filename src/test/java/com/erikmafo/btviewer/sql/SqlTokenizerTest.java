package com.erikmafo.btviewer.sql;

import org.junit.Test;

import static org.junit.Assert.*;

public class SqlTokenizerTest {

    @Test
    public void shouldReadSelectStatement() {

        var reader = new SqlTokenizer(
                "SELECT firstVar.foo, secondVar FROM 'table' WHERE firstVar > 0 AND secondVar = 'test' LIMIT 10");

        assertEquals(SqlTokenType.SELECT, reader.next().getTokenType());
        assertEquals(SqlTokenType.IDENTIFIER, reader.next().getTokenType());
        assertEquals(SqlTokenType.COMMA, reader.next().getTokenType());
        assertEquals(SqlTokenType.IDENTIFIER, reader.next().getTokenType());
        assertEquals(SqlTokenType.FROM, reader.next().getTokenType());
        assertEquals(SqlTokenType.QUOTED_STRING, reader.next().getTokenType());
        assertEquals(SqlTokenType.WHERE, reader.next().getTokenType());
        assertEquals(SqlTokenType.IDENTIFIER, reader.next().getTokenType());
        assertEquals(SqlTokenType.OPERATOR, reader.next().getTokenType());
        assertEquals(SqlTokenType.INTEGER, reader.next().getTokenType());
        assertEquals(SqlTokenType.AND, reader.next().getTokenType());
        assertEquals(SqlTokenType.IDENTIFIER, reader.next().getTokenType());
        assertEquals(SqlTokenType.OPERATOR, reader.next().getTokenType());
        assertEquals(SqlTokenType.QUOTED_STRING, reader.next().getTokenType());
        assertEquals(SqlTokenType.LIMIT, reader.next().getTokenType());
        assertEquals(SqlTokenType.INTEGER, reader.next().getTokenType());
        assertNull(reader.next());
    }

    @Test
    public void shouldParseFieldWithFamilyAndQualifier() {

        var reader = new SqlTokenizer("family.qualifier");
        var token = reader.next();

        assertEquals(SqlTokenType.IDENTIFIER, token.getTokenType());
        assertEquals("family.qualifier", token.getValue());
    }

    @Test
    public void shouldParseFieldWithFamily() {

        var reader = new SqlTokenizer("family");
        var token = reader.next();

        assertEquals(SqlTokenType.IDENTIFIER, token.getTokenType());
        assertEquals("family", token.getValue());
    }

    @Test
    public void shouldProduceInvalidTokenIfInvalidIdentifier() {
        var reader = new SqlTokenizer("family.");
        reader.next();
        var token = reader.next();
        assertEquals(SqlTokenType.INVALID, token.getTokenType());
    }

    @Test
    public void shouldReturnInvalidTokenGivenInvalidCharacter() {
        var reader = new SqlTokenizer("§");
        var token = reader.next();
        assertEquals(SqlTokenType.INVALID, token.getTokenType());
    }
}
