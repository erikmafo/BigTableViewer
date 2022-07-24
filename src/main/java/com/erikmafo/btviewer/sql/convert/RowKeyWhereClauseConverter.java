package com.erikmafo.btviewer.sql.convert;

import com.erikmafo.btviewer.sql.WhereClause;
import com.google.cloud.bigtable.data.v2.models.Range;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RowKeyWhereClauseConverter {

    private String singleRowKey;
    private Range.ByteStringRange rowRange = Range.ByteStringRange.unbounded();

    @NotNull
    public RowKeyWhereClauseConverter addMany(@NotNull Collection<WhereClause> whereClauses) {
        for (var whereClause : whereClauses) {
            add(whereClause);
        }
        return this;
    }

    @NotNull
    public BigtableRowRange convertToRowRange() {
        return new BigtableRowRange(singleRowKey, rowRange);
    }

    private void add(@NotNull WhereClause whereClause) {
        switch (whereClause.getOperator()) {
            case EQUAL:
                singleRowKey = whereClause.getValue().asString();
                break;
            case LESS_THAN:
                rowRange.endOpen(whereClause.getValue().asString());
                break;
            case LESS_THAN_OR_EQUAL:
                rowRange.endClosed(whereClause.getValue().asString());
                break;
            case GREATER_THAN:
                rowRange.startOpen(whereClause.getValue().asString());
                break;
            case GREATER_THAN_OR_EQUAL:
                rowRange.startClosed(whereClause.getValue().asString());
                break;
            default:
                break;
        }
    }
}
