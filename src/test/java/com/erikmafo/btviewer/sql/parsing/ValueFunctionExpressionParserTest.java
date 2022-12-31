package com.erikmafo.btviewer.sql.parsing;

import com.erikmafo.btviewer.sql.parsing.SqlTokenizer;
import com.erikmafo.btviewer.sql.parsing.ValueFunctionExpressionParser;
import com.erikmafo.btviewer.sql.query.ValueType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueFunctionExpressionParserTest {

    @Test
    public void shouldReverseString() {
        var token = new SqlTokenizer("REVERSE('foo')").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.valueType());
        assertEquals("oof", value.asString());
    }

    @Test
    public void shouldReverseStringTwice() {
        var token = new SqlTokenizer("REVERSE(REVERSE('foo'))").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.valueType());
        assertEquals("foo", value.asString());
    }

    @Test
    public void shouldConcatStrings() {
        var token = new SqlTokenizer("CONCAT('foo', 'bar')").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.valueType());
        assertEquals("foobar", value.asString());
    }

    @Test
    public void shouldConcatStringAndNumber() {
        var token = new SqlTokenizer("CONCAT('foo', 123)").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.valueType());
        assertEquals("foo123", value.asString());
    }

    @Test
    public void shouldConcatWithReverseArgument() {
        var token = new SqlTokenizer("CONCAT(REVERSE('foo'), 'bar')").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.valueType());
        assertEquals("oofbar", value.asString());
    }
}
