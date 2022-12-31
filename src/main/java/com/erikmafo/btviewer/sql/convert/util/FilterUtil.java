package com.erikmafo.btviewer.sql.convert.util;

import com.google.cloud.bigtable.data.v2.models.Filters;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class FilterUtil {

    public static Filters.Filter chain(@NotNull List<Filters.Filter> filters) {
        if (filters.isEmpty()) {
            return FILTERS.pass();
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        var filter = FILTERS.chain();
        for (var tsFilter : filters) {
            filter.filter(tsFilter);
        }
        return filter;
    }

    public static Filters.Filter interleave(@NotNull List<Filters.Filter> filters) {
        if (filters.isEmpty()) {
            return FILTERS.pass();
        }
        if (filters.size() == 1) {
            return filters.get(0);
        }
        var interleaveFilter = FILTERS.interleave();
        for (var filter : filters) {
            interleaveFilter.filter(filter);
        }
        return interleaveFilter;
    }
}
