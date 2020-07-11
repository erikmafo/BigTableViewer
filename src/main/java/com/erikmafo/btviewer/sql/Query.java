package com.erikmafo.btviewer.sql;

import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

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
        bigtableQuery.filter(getFilter());
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
                default:
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

    private List<WhereClause> filterTimestampWhereClauses() {
        return whereClauses
                .stream()
                .filter(where -> where.getField().isTimestamp())
                .collect(Collectors.toList());
    }

    private Filters.Filter getFilter() {
        return chain(Arrays.asList(
                getRowKeyRegexFilter(),
                getTimestampFilter(),
                getValueFilter(),
                getFamilyQualifierFilter()));
    }

    private Filters.Filter getRowKeyRegexFilter() {
        var filters = getWhereClauses().stream()
                .filter(w -> w.getField().isRowKey())
                .filter(w -> w.getOperator().equals(Operator.LIKE))
                .map(w -> FILTERS.key().regex(w.getValue().asString()))
                .collect(Collectors.toList());
        return chain(filters);
    }

    private Filters.Filter getValueFilter() {
        var filters = getWhereClauses().stream()
                .filter(w -> !w.getField().isTimestamp())
                .filter(w -> !w.getField().isRowKey())
                .filter(w -> w.getValue().getValueType().equals(ValueType.STRING))
                .map(this::getValueFilter)
                .collect(Collectors.toList());
        return chain(filters);
    }

    private Filters.Filter getValueFilter(WhereClause where) {
        var condition = FILTERS.chain()
                .filter(FILTERS.chain()
                        .filter(getFamilyQualifierFilter(where.getField()))
                        .filter(getValueFilter(where.getOperator(), where.getValue()))
                        .filter(FILTERS.limit().cellsPerColumn(1)))
                .filter(FILTERS.offset().cellsPerRow(1));
        return FILTERS
                .condition(condition)
                .then(FILTERS.pass())
                .otherwise(FILTERS.block());
    }

    private Filters.Filter getValueFilter(Operator operator, Value value) {
        switch (operator) {
            case EQUAL:
                return FILTERS.value().exactMatch(value.asString());
            case LESS_THAN:
                return FILTERS.value().range().endOpen(value.asString());
            case LESS_THAN_OR_EQUAL:
                return FILTERS.value().range().endClosed(value.asString());
            case GREATER_THAN:
                return FILTERS.value().range().startOpen(value.asString());
            case GREATER_THAN_OR_EQUAL:
                return FILTERS.value().range().startClosed(value.asString());
            case LIKE:
                return FILTERS.value().regex(value.asString());
            default:
                throw new IllegalArgumentException(
                        String.format("operator %s is not supported for filtering values", operator));
        }
    }

    private Filters.Filter getFamilyQualifierFilter() {
        var filters = new LinkedList<Filters.Filter>();
        filters.addAll(getFamilyFilters());
        filters.addAll(getQualifierFilters());
        return interleave(filters);
    }

    private List<Filters.Filter> getFamilyFilters() {
        return fields.stream()
                .filter(f -> !f.isAsterisk())
                .filter(f -> !f.hasQualifier())
                .map(f -> FILTERS.family().exactMatch(f.getFamily()))
                .collect(Collectors.toList());
    }

    private List<Filters.Filter> getQualifierFilters() {
        return fields
                .stream()
                .filter(f -> !f.isAsterisk())
                .filter(Field::hasQualifier)
                .map(this::getFamilyQualifierFilter)
                .collect(Collectors.toList());
    }

    private Filters.Filter getFamilyQualifierFilter(Field field) {
        return FILTERS.chain()
                .filter(FILTERS.family().exactMatch(field.getFamily()))
                .filter(FILTERS.qualifier().exactMatch(field.getQualifier()));
    }

    private Filters.Filter getTimestampFilter() {
        return chain(getTimestampFilters());
    }

    private Filters.Filter interleave(List<Filters.Filter> filters) {
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

    private Filters.Filter chain(List<Filters.Filter> filters) {
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

    private List<Filters.Filter> getTimestampFilters() {
        var filters = new LinkedList<Filters.Filter>();
        for (var where : filterTimestampWhereClauses()) {
            var timestamp = toTimestamp(where.getValue());
            switch (where.getOperator()) {
                case EQUAL:
                    filters.add(FILTERS.timestamp().exact(timestamp));
                    break;
                case LESS_THAN:
                    filters.add(FILTERS.timestamp().range().endClosed(timestamp));
                    break;
                case LESS_THAN_OR_EQUAL:
                    filters.add(FILTERS.timestamp().range().endOpen(timestamp));
                    break;
                case GREATER_THAN:
                    filters.add(FILTERS.timestamp().range().startClosed(timestamp));
                    break;
                case GREATER_THAN_OR_EQUAL:
                    filters.add(FILTERS.timestamp().range().startOpen(timestamp));
                    break;
                case LIKE:
                default:
                    throw new IllegalArgumentException(String.format(
                            "Operator %s is not supported for timestamps",
                            where.getOperator()));
            }
        }
        return filters;
    }

    private long toTimestamp(Value value) {
        long millis;
        switch (value.getValueType()) {
            case STRING:
                millis = DateTimeFormatUtil.toMicros(value.asString());
                break;
            case NUMBER:
                millis = value.asLong();
                break;
            default: throw new IllegalArgumentException(
                    String.format(
                            "Could not parse %s to millis. Must be integer or date time string",
                            value.asString()));
        }
        return millis;
    }
}
