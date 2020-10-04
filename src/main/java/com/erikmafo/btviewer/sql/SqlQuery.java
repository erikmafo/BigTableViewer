package com.erikmafo.btviewer.sql;

import java.util.ArrayList;
import java.util.List;

public class SqlQuery {

    public static String getDefaultSql(String tableName) {
        return String.format("SELECT * FROM '%s' LIMIT 1000", tableName);
    }

    private QueryType queryType;
    private String tableName;
    private final List<Field> fields = new ArrayList<>();
    private final List<WhereClause> whereClauses = new ArrayList<>();
    private int limit = 10_000;

    public void addField(Field field) {
        fields.add(field);
    }

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

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }

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
}
