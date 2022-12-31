package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.convert.util.FilterUtil;
import com.erikmafo.btviewer.sql.query.Operator;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.google.cloud.bigtable.data.v2.models.Filters;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class RowKeyFilterConverter implements SqlQueryFilterConverter {

    public Filters.Filter convert(@NotNull SqlQuery sqlQuery) {
        return FilterUtil.chain(getRowKeyFilters(sqlQuery));
    }

    @NotNull
    private static List<Filters.Filter> getRowKeyFilters(@NotNull SqlQuery sqlQuery) {
        return sqlQuery
                .whereClauses()
                .stream()
                .filter(w -> w.field().isRowKey())
                .filter(w -> w.operator().equals(Operator.LIKE))
                .map(w -> FILTERS.key().regex(w.getValue().asString()))
                .collect(Collectors.toList());
    }
}
