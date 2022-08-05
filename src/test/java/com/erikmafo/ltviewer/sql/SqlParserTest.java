package com.erikmafo.ltviewer.sql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlParserTest {

    private SqlParser parser = new SqlParser();

    @Test
    public void shouldParseSelectStatement() {

        var query = parser.parse(
                "SELECT firstVar, secondVar FROM 'table' WHERE firstVar > 0 AND secondVar = 'test' LIMIT 1000");

        assertEquals(QueryType.SELECT, query.getQueryType());
    }

    @Test
    public void shouldParseSelectStatementWithAsterisk() {
        var query = parser.parse(
                "SELECT * FROM 'table' LIMIT 1000");

        assertEquals(QueryType.SELECT, query.getQueryType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenInvalidCharacters() {
        parser.parse("SELECT * FROM 'table-0' LIMIT 100§");
    }
}
