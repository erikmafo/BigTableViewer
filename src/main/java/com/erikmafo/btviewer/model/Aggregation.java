package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.functions.AggregationExpression;

public class Aggregation {

    private final AggregationExpression.Type type;
    private final String fieldName;

    private int count;
    private double sum;

    public Aggregation(AggregationExpression.Type type, String fieldName) {
        this.type = type;
        this.fieldName = fieldName;
    }

    public void updateFrom(Aggregation other) {
        if (other.type != type) {
            throw new AssertionError(
                    String.format("Expected aggregation types to be equal but was %s and %s", type, other.type));
        }
        switch (type) {
            case COUNT:
                count += other.count;
                break;
            case SUM:
                updateSum(other.sum);
                break;
            case AVG:
                updateSum(other.sum);
                incrementCount();
                break;
        }
    }

    public String getName() {
        return String.format("%s(%s)", type.name(), fieldName);
    }

    public void updateSum(double addToSum) {
        sum += addToSum;
    }

    public void incrementCount() {
        count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Number getValue() {
        switch (type) {
            case COUNT: return count;
            case SUM: return sum;
            case AVG: return count > 0 ? sum / count : 0;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
