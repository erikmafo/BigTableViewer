package com.erikmafo.ltviewer.sql.functions;

import com.erikmafo.ltviewer.sql.SqlTokenizer;
import com.erikmafo.ltviewer.sql.ValueType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueFunctionExpressionParserTest {

    @Test
    public void shouldReverseString() {
        var token = new SqlTokenizer("REVERSE('foo')").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.getValueType());
        assertEquals("oof", value.asString());
    }

    @Test
    public void shouldReverseStringTwice() {
        var token = new SqlTokenizer("REVERSE(REVERSE('foo'))").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.getValueType());
        assertEquals("foo", value.asString());
    }

    @Test
    public void shouldConcatStrings() {
        var token = new SqlTokenizer("CONCAT('foo', 'bar')").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.getValueType());
        assertEquals("foobar", value.asString());
    }

    @Test
    public void shouldConcatStringAndNumber() {
        var token = new SqlTokenizer("CONCAT('foo', 123)").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.getValueType());
        assertEquals("foo123", value.asString());
    }

    @Test
    public void shouldConcatWithReverseArgument() {
        var token = new SqlTokenizer("CONCAT(REVERSE('foo'), 'bar')").next();
        var value = ValueFunctionExpressionParser.parse(token.getSubTokens());
        assertEquals(ValueType.STRING, value.getValueType());
        assertEquals("oofbar", value.asString());
    }
}
