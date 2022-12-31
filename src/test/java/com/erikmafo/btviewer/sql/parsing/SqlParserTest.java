package com.erikmafo.btviewer.sql.parsing;

import com.erikmafo.btviewer.sql.query.QueryType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlParserTest {

    private SqlParser parser = new SqlParser();

    @Test
    public void shouldParseSelectStatement() {

        var query = parser.parse(
                "SELECT firstVar, secondVar FROM 'table' WHERE firstVar > 0 AND secondVar = 'test' LIMIT 1000");

        assertEquals(QueryType.SELECT, query.queryType());
    }

    @Test
    public void shouldParseSelectValuesInStatement() {

        var query = parser.parse(
                "SELECT firstVar FROM 'table' WHERE firstVar IN (1, 2, 3)");

        assertEquals(QueryType.SELECT, query.queryType());
        assertEquals(3, query.whereClauses().stream().findFirst().get().values().stream().count());
    }

    @Test
    public void shouldParseSelectStatementWithAsterisk() {
        var query = parser.parse(
                "SELECT * FROM 'table' LIMIT 1000");

        assertEquals(QueryType.SELECT, query.queryType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenInvalidCharacters() {
        parser.parse("SELECT * FROM 'table-0' LIMIT 100§");
    }
}
