package com.erikmafo.btviewer.sql.query;

import com.erikmafo.btviewer.sql.parsing.LiteralValues;
import com.erikmafo.btviewer.sql.parsing.SqlToken;
import com.erikmafo.btviewer.sql.parsing.SqlTokenType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueTest {

    @Test
    public void from_integerSqlToken_returnsValueOfTypeNumber() {
        // given
        var sqlToken = new SqlToken("42", SqlTokenType.NUMBER, 0);

        // when
        var value = LiteralValues.from(sqlToken);

        // then
        assertEquals(ValueType.NUMBER, value.valueType());
        assertEquals(42, value.asInt());
    }

    @Test
    public void asDouble_returnsValueAsDouble() {
        var value = new Value("42.3", ValueType.NUMBER);

        assertEquals(42.3, value.asDouble(), Double.MIN_VALUE);
    }

    @Test
    public void asLong_returnsValueAsLong() {
        var value = new Value("42", ValueType.NUMBER);

        assertEquals(42, value.asLong());
    }

    @Test
    public void asString_returnsValueAsString() {
        var value = new Value("42", ValueType.STRING);

        assertEquals("42", value.asString());
    }
}