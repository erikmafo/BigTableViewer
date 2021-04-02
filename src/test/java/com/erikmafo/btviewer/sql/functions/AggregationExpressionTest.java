package com.erikmafo.btviewer.sql.functions;

import com.erikmafo.btviewer.sql.SqlTokenizer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class AggregationExpressionTest {

    @Test
    @Parameters({
            "SUM(foo.bar), SUM",
            "COUNT(foo.bar), COUNT",
            "AVG(foo.bar), AVG"})
    public void shouldEvaluateAggregationExpression(String expression, Aggregation.Type aggregationType) {
        var tokens = new SqlTokenizer(expression).next().getSubTokens();
        var aggregation = AggregationExpression.evaluate(tokens);

        assertEquals(aggregationType, aggregation.getType());
        assertEquals("foo.bar", aggregation.getField().getName());
    }

    @Test
    @Parameters({
            "SUM(*)",
            "SUM(foo)",
            "AVG(*)",
            "AVG(foo)"})
    public void shouldThrowIfFamilyOrQualifierIsUnspecified(String expression) {
        var tokens = new SqlTokenizer(expression).next().getSubTokens();
        assertThrows(IllegalArgumentException.class, () -> AggregationExpression.evaluate(tokens));
    }

    @Test
    public void shouldAllowWildcardWithCount() {
        var tokens = new SqlTokenizer("COUNT(*)").next().getSubTokens();
        var aggregation = AggregationExpression.evaluate(tokens);
        assertEquals(Aggregation.Type.COUNT, aggregation.getType());
        assertTrue(aggregation.getField().isAsterisk());
    }
}
