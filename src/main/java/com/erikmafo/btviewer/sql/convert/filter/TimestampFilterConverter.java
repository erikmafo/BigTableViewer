package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.convert.util.FilterUtil;
import com.erikmafo.btviewer.sql.query.Operator;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.erikmafo.btviewer.sql.query.WhereClause;
import com.google.cloud.bigtable.data.v2.models.Filters;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class TimestampFilterConverter implements SqlQueryFilterConverter {

    public Filters.Filter convert(@NotNull SqlQuery sqlQuery) {
        return FilterUtil.chain(getTimestampFilters(sqlQuery.whereClauses()));
    }

    @NotNull
    private List<Filters.Filter> getTimestampFilters(@NotNull Collection<WhereClause> whereClauses) {
        return whereClauses
                .stream()
                .filter(TimestampFilterConverter::isTimestampExpression)
                .map(TimestampFilterConverter::getTimestampFilter)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static Filters.Filter getTimestampFilter(@NotNull WhereClause where) {
        return getTimestampFilter(where.operator(), where.getValue().asTimestamp());
    }

    private static Filters.Filter getTimestampFilter(Operator operator, long timestamp) {
        return switch (operator) {
            case EQUAL -> FILTERS.timestamp().exact(timestamp);
            case LESS_THAN -> FILTERS.timestamp().range().endClosed(timestamp);
            case LESS_THAN_OR_EQUAL -> FILTERS.timestamp().range().endOpen(timestamp);
            case GREATER_THAN -> FILTERS.timestamp().range().startClosed(timestamp);
            case GREATER_THAN_OR_EQUAL -> FILTERS.timestamp().range().startOpen(timestamp);
            default -> throw new IllegalArgumentException(
                    String.format("Operator %s is not supported for timestamps", operator));
        };
    }

    private static boolean isTimestampExpression(@NotNull WhereClause whereClause) {
        return whereClause.field().isTimestamp();
    }
}
