package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.convert.util.FilterUtil;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.google.cloud.bigtable.data.v2.models.Filters;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class ChainedFilterConverter implements SqlQueryFilterConverter {

    private final Collection<SqlQueryFilterConverter> sqlQueryFilterConverters;

    public ChainedFilterConverter(SqlQueryFilterConverter... sqlQueryFilterConverters) {
        this.sqlQueryFilterConverters = Arrays.asList(sqlQueryFilterConverters);
    }

    @Override
    public Filters.Filter convert(@NotNull SqlQuery sqlQuery) {
        return FilterUtil.chain(getFilters(sqlQuery));
    }

    private List<Filters.Filter> getFilters(@NotNull SqlQuery sqlQuery) {
        return sqlQueryFilterConverters
                .stream()
                .map(converter -> converter.convert(sqlQuery))
                .filter(f -> !f.equals(FILTERS.pass()))
                .collect(Collectors.toList());
    }
}
