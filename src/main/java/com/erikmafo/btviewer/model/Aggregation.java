package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.sql.functions.AggregationExpression;
import com.erikmafo.btviewer.util.Check;
import org.jetbrains.annotations.NotNull;

public class Aggregation {

    private final AggregationExpression.Type type;
    private final String fieldName;

    private int count;
    private double sum;

    public Aggregation(AggregationExpression.Type type, String fieldName) {
        Check.notNull(type, "type");
        Check.notNullOrEmpty(fieldName, "fieldName");

        this.type = type;
        this.fieldName = fieldName;
    }

    public void updateFrom(@NotNull Aggregation other) {
        if (other.type != type) {
            throw new IllegalArgumentException(
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
            default:
                throw new IllegalArgumentException(String.format("Unsupported aggregation type: %s", type));
        }
    }

    public String getName() {
        return String.format("%s(%s)", type.name(), fieldName);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSum(double sum) { this.sum = sum; }

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

    private void updateSum(double addToSum) {
        sum += addToSum;
    }

    private void incrementCount() {
        count++;
    }
}
