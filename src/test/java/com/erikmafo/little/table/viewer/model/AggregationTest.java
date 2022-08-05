package com.erikmafo.little.table.viewer.model;

import com.erikmafo.little.table.viewer.sql.functions.AggregationExpression;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AggregationTest {

    @Test
    public void shouldIncrementCount() {
        //given
        var first = createAggregation(AggregationExpression.Type.COUNT);
        first.setCount(1);
        var second = createAggregation(AggregationExpression.Type.COUNT);
        second.setCount(1);

        //when
        first.updateFrom(second);

        //then
        assertEquals(2, first.getValue());
    }

    @Test
    public void shouldAddSums() {
        //given
        var first = createAggregation(AggregationExpression.Type.SUM);
        first.setSum(1.0);
        var second = createAggregation(AggregationExpression.Type.SUM);
        second.setSum(2.5);

        //when
        first.updateFrom(second);

        //then
        assertEquals(3.5, first.getValue());
    }

    @Test
    public void shouldComputeAverage() {
        //given
        var first = createAggregation(AggregationExpression.Type.AVG);
        first.setCount(3);
        first.setSum(1);
        var second = createAggregation(AggregationExpression.Type.AVG);
        second.setCount(1);
        second.setSum(3);

        //when
        first.updateFrom(second);

        //then
        assertEquals(1.0, first.getValue());
    }

    @NotNull
    private Aggregation createAggregation(AggregationExpression.Type type) {
        return new Aggregation(type, "foo");
    }
}