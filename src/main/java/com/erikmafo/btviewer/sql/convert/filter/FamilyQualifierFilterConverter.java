package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.convert.util.FilterUtil;
import com.erikmafo.btviewer.sql.query.Field;
import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.google.cloud.bigtable.data.v2.models.Filters;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class FamilyQualifierFilterConverter implements SqlQueryFilterConverter {

    public Filters.Filter convert(@NotNull SqlQuery sqlQuery) {
        var filters = new LinkedList<Filters.Filter>();
        filters.addAll(getFamilyFilters(sqlQuery));
        filters.addAll(getQualifierFilters(sqlQuery));
        return FilterUtil.interleave(filters);
    }

    private List<Filters.Filter> getFamilyFilters(@NotNull SqlQuery sqlQuery) {
        return sqlQuery
                .fields()
                .stream()
                .filter(f -> !f.isAsterisk())
                .filter(f -> !f.hasQualifier())
                .map(f -> FILTERS.family().exactMatch(f.getFamily()))
                .collect(Collectors.toList());
    }

    @NotNull
    private Filters.Filter getFamilyQualifierFilter(@NotNull Field field) {
        return FILTERS
                .chain()
                .filter(FILTERS.family().exactMatch(field.getFamily()))
                .filter(FILTERS.qualifier().exactMatch(field.getQualifier()));
    }

    private List<Filters.Filter> getQualifierFilters(@NotNull SqlQuery sqlQuery) {
        return sqlQuery
                .fields()
                .stream()
                .filter(f -> !f.isAsterisk())
                .filter(Field::hasQualifier)
                .map(this::getFamilyQualifierFilter)
                .collect(Collectors.toList());
    }
}
