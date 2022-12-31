package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.google.cloud.bigtable.data.v2.models.Filters;

public interface SqlQueryFilterConverter {

    /**
     * Creates a filter that covers a part of the given sql query
     * @param sqlQuery a sql query.
     * @return a bigtable filter.
     */
    Filters.Filter convert(SqlQuery sqlQuery);
}
