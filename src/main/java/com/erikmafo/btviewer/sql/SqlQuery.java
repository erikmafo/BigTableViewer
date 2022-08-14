package com.erikmafo.btviewer.sql;

import com.erikmafo.btviewer.sql.functions.AggregationExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a Sql query expression.
 */
public class SqlQuery {

    private final List<Field> fields = new ArrayList<>();
    private final List<AggregationExpression> aggregationExpressions = new ArrayList<>();
    private final List<WhereClause> whereClauses = new ArrayList<>();
    private QueryType queryType;
    private String tableName;
    private int limit = Integer.MAX_VALUE;

    public static String getDefaultSqlQuery(String tableName) {
        return String.format("SELECT * FROM '%s' LIMIT 1000", tableName);
    }

    public void addField(Field field) { fields.add(field); }

    public void addFields(Collection<Field> fields) { this.fields.addAll(fields); }

    public void addWhereClause(WhereClause whereClause) {
        whereClauses.add(whereClause);
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public List<Field> getFields() {
        return fields;
    }

    public List<WhereClause> getWhereClauses() {
        return whereClauses;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public SqlQuery ensureValid() {
        if (tableName == null) {
            throw new IllegalArgumentException("Missing table name");
        }

        return this;
    }

    public boolean isAggregation() {
        return !aggregationExpressions.isEmpty();
    }

    public List<AggregationExpression> getAggregations() {
        return aggregationExpressions;
    }

    public void addAggregation(AggregationExpression aggregationExpression) {
        this.aggregationExpressions.add(aggregationExpression);
    }
}
