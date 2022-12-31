package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.query.AggregationType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AggregationTest {

    @Test
    public void shouldIncrementCount() {
        //given
        var first = createAggregation(AggregationType.COUNT);
        first.setCount(1);
        var second = createAggregation(AggregationType.COUNT);
        second.setCount(1);

        //when
        first.updateFrom(second);

        //then
        assertEquals(2, first.getValue());
    }

    @Test
    public void shouldAddSums() {
        //given
        var first = createAggregation(AggregationType.SUM);
        first.setSum(1.0);
        var second = createAggregation(AggregationType.SUM);
        second.setSum(2.5);

        //when
        first.updateFrom(second);

        //then
        assertEquals(3.5, first.getValue());
    }

    @Test
    public void shouldComputeAverage() {
        //given
        var first = createAggregation(AggregationType.AVG);
        first.setCount(3);
        first.setSum(1);
        var second = createAggregation(AggregationType.AVG);
        second.setCount(1);
        second.setSum(3);

        //when
        first.updateFrom(second);

        //then
        assertEquals(1.0, first.getValue());
    }

    @NotNull
    private com.erikmafo.btviewer.model.Aggregation createAggregation(AggregationType type) {
        return new com.erikmafo.btviewer.model.Aggregation(type, "foo");
    }
}