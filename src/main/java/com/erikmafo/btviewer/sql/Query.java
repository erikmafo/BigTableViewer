package com.erikmafo.btviewer.sql;

import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Range;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Query {

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

    public com.google.cloud.bigtable.data.v2.models.Query toBigtableQuery() {
        var bigtableQuery = com.google.cloud.bigtable.data.v2.models.Query.create(tableName);
        setRowRange(bigtableQuery);
        bigtableQuery.limit(getLimit());
        return bigtableQuery;
    }

    private void setRowRange(com.google.cloud.bigtable.data.v2.models.Query bigtableQuery) {
        WhereClause rowKeyEq = null;
        var rowRange = Range.ByteStringRange.unbounded();
        for (var whereClause : filterRowKeyWhereClauses()) {
            switch (whereClause.getOperator()) {
                case EQUAL:
                    rowKeyEq = whereClause;
                    break;
                case LESS_THAN:
                    rowRange.endClosed(whereClause.getValue().asString());
                    break;
                case LESS_THAN_OR_EQUAL:
                    rowRange.endOpen(whereClause.getValue().asString());
                    break;
                case GREATER_THAN:
                    rowRange.startOpen(whereClause.getValue().asString());
                    break;
                case GREATER_THAN_OR_EQUAL:
                    rowRange.startClosed(whereClause.getValue().asString());
                    break;
                case LIKE:
                    bigtableQuery.filter(Filters.FILTERS.key().regex(whereClause.getValue().asString()));
                    break;
            }
        }

        if (rowKeyEq != null) {
            bigtableQuery.rowKey(rowKeyEq.getValue().asString());
        } else {
            bigtableQuery.range(rowRange);
        }
    }

    private List<WhereClause> filterRowKeyWhereClauses() {
        return whereClauses
                .stream()
                .filter(where -> where.getField().isRowKey())
                .collect(Collectors.toList());
    }
}
