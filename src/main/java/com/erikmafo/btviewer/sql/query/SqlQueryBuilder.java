package com.erikmafo.btviewer.sql.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SqlQueryBuilder {

    private final List<Field> fields = new ArrayList<>();
    private final List<AggregationExpression> aggregationExpressions = new ArrayList<>();
    private final List<WhereClause> whereClauses = new ArrayList<>();
    private QueryType queryType;
    private String tableName;
    private int limit = Integer.MAX_VALUE;

    public void addField(Field field) { fields.add(field); }

    public void addFields(Collection<Field> fields) { this.fields.addAll(fields); }

    public void addWhereClause(WhereClause whereClause) {
        whereClauses.add(whereClause);
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void addAggregationExpression(AggregationExpression aggregationExpression) {
        this.aggregationExpressions.add(aggregationExpression);
    }

    public SqlQuery build() {
        if (tableName == null) {
            throw new IllegalArgumentException("Missing table name");
        }

        return new SqlQuery(
                queryType,
                tableName,
                fields,
                aggregationExpressions,
                whereClauses,
                limit
        );
    }
}
