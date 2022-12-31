package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.query.AggregationType;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.google.cloud.bigtable.data.v2.models.Filters;
import org.jetbrains.annotations.NotNull;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class AggregationFilterConverter implements SqlQueryFilterConverter {

    public Filters.Filter convert(@NotNull SqlQuery sqlQuery) {
        if (sqlQuery.aggregationExpressions().isEmpty()) {
            return FILTERS.pass();
        }

        if (sqlQuery.aggregationExpressions().stream().allMatch(a -> a.type() == AggregationType.COUNT)) {
            return FILTERS.chain()
                    .filter(FILTERS.limit().cellsPerColumn(1))
                    .filter(FILTERS.value().strip());
        }

        return FILTERS.limit().cellsPerColumn(1);
    }
}
