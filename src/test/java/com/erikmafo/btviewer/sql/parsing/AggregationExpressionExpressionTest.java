package com.erikmafo.btviewer.sql.parsing;

import com.erikmafo.btviewer.sql.query.AggregationType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class AggregationExpressionExpressionTest {

    @Test
    @Parameters({
            "SUM(foo.bar), SUM",
            "COUNT(foo.bar), COUNT",
            "AVG(foo.bar), AVG"})
    public void shouldEvaluateAggregationExpression(String expression, AggregationType aggregationType) {
        var tokens = new SqlTokenizer(expression).next().getSubTokens();
        var aggregation = AggregationExpressionParser.parse(tokens);

        assertEquals(aggregationType, aggregation.type());
        assertEquals("foo.bar", aggregation.field().name());
    }

    @Test
    @Parameters({
            "SUM(*)",
            "SUM(foo)",
            "AVG(*)",
            "AVG(foo)"})
    public void shouldThrowIfFamilyOrQualifierIsUnspecified(String expression) {
        var tokens = new SqlTokenizer(expression).next().getSubTokens();
        assertThrows(IllegalArgumentException.class, () -> AggregationExpressionParser.parse(tokens));
    }

    @Test
    public void shouldAllowWildcardWithCount() {
        var tokens = new SqlTokenizer("COUNT(*)").next().getSubTokens();
        var aggregation = AggregationExpressionParser.parse(tokens);
        assertEquals(AggregationType.COUNT, aggregation.type());
        assertTrue(aggregation.field().isAsterisk());
    }
}
