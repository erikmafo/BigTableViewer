package com.erikmafo.btviewer.sql;

import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Range;
import com.google.protobuf.ByteString;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;

public class QueryConverter {
    private final ByteStringConverter byteStringConverter;

    public QueryConverter(ByteStringConverter byteStringConverter) {
        this.byteStringConverter = byteStringConverter;
    }

    public Query toBigtableQuery(SqlQuery sqlQuery) {
        var bigtableQuery = Query.create(sqlQuery.getTableName());
        setRowRange(bigtableQuery, sqlQuery);
        bigtableQuery.filter(getFilter(sqlQuery));
        bigtableQuery.limit(sqlQuery.getLimit());
        return bigtableQuery;
    }

    private void setRowRange(Query bigtableQuery, SqlQuery sqlQuery) {
        WhereClause rowKeyEq = null;
        var rowRange = Range.ByteStringRange.unbounded();
        for (var whereClause : filterRowKeyWhereClauses(sqlQuery)) {
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

    private List<WhereClause> filterRowKeyWhereClauses(SqlQuery sqlQuery) {
        return sqlQuery.getWhereClauses()
                .stream()
                .filter(where -> where.getField().isRowKey())
                .collect(Collectors.toList());
    }

    private List<WhereClause> filterTimestampWhereClauses(SqlQuery sqlQuery) {
        return sqlQuery.getWhereClauses()
                .stream()
                .filter(where -> where.getField().isTimestamp())
                .collect(Collectors.toList());
    }

    private Filters.Filter getFilter(SqlQuery sqlQuery) {
        return chain(Arrays.asList(
                getRowKeyRegexFilter(sqlQuery),
                getTimestampFilter(sqlQuery),
                getValueFilter(sqlQuery),
                getFamilyQualifierFilter(sqlQuery)));
    }

    private Filters.Filter getRowKeyRegexFilter(SqlQuery sqlQuery) {
        var filters = sqlQuery.getWhereClauses().stream()
                .filter(w -> w.getField().isRowKey())
                .filter(w -> w.getOperator().equals(Operator.LIKE))
                .map(w -> FILTERS.key().regex(w.getValue().asString()))
                .collect(Collectors.toList());
        return chain(filters);
    }

    private Filters.Filter getValueFilter(SqlQuery sqlQuery) {
        var filters = sqlQuery.getWhereClauses().stream()
                .filter(w -> !w.getField().isTimestamp())
                .filter(w -> !w.getField().isRowKey())
                .map(this::getValueFilter)
                .collect(Collectors.toList());
        return chain(filters);
    }

    private Filters.Filter getValueFilter(WhereClause where) {
        var byteString = getValueByteString(where);
        var trueFilter = FILTERS.pass();
        var falseFilter = FILTERS.block();
        Filters.Filter valueFilter;

        switch (where.getOperator()) {
            case NOT_EQUAL:
                trueFilter = FILTERS.block();
                falseFilter = FILTERS.pass();
            case EQUAL:
                valueFilter = FILTERS.value().exactMatch(byteString);
                break;
            case LESS_THAN:
                valueFilter = FILTERS.value().range().endOpen(byteString);
                break;
            case LESS_THAN_OR_EQUAL:
                valueFilter = FILTERS.value().range().endClosed(byteString);
                break;
            case GREATER_THAN:
                valueFilter = FILTERS.value().range().startOpen(byteString);
                break;
            case GREATER_THAN_OR_EQUAL:
                valueFilter = FILTERS.value().range().startClosed(byteString);
                break;
            case LIKE:
                valueFilter = FILTERS.value().regex(where.getValue().asString());
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("operator %s is not supported for filtering values", where.getOperator()));
        }

        var condition = FILTERS.chain()
                .filter(FILTERS.chain()
                        .filter(FILTERS.limit().cellsPerColumn(1))
                        .filter(FILTERS.family().exactMatch(where.getField().getFamily()))
                        .filter(FILTERS.qualifier().exactMatch(where.getField().getQualifier()))
                        .filter(valueFilter)
                        .filter(FILTERS.limit().cellsPerRow(1)));
        return FILTERS
                .condition(condition)
                .then(trueFilter)
                .otherwise(falseFilter);
    }

    private ByteString getValueByteString(WhereClause where) {
        return byteStringConverter.toByteString(where.getField(), where.getValue());
    }

    private Filters.Filter getFamilyQualifierFilter(SqlQuery sqlQuery) {
        var filters = new LinkedList<Filters.Filter>();
        filters.addAll(getFamilyFilters(sqlQuery));
        filters.addAll(getQualifierFilters(sqlQuery));
        return interleave(filters);
    }

    private List<Filters.Filter> getFamilyFilters(SqlQuery sqlQuery) {
        return sqlQuery.getFields().stream()
                .filter(f -> !f.isAsterisk())
                .filter(f -> !f.hasQualifier())
                .map(f -> FILTERS.family().exactMatch(f.getFamily()))
                .collect(Collectors.toList());
    }

    private List<Filters.Filter> getQualifierFilters(SqlQuery sqlQuery) {
        return sqlQuery.getFields()
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

    private Filters.Filter getTimestampFilter(SqlQuery sqlQuery) {
        return chain(getTimestampFilters(sqlQuery));
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

    private List<Filters.Filter> getTimestampFilters(SqlQuery sqlQuery) {
        var filters = new LinkedList<Filters.Filter>();
        for (var where : filterTimestampWhereClauses(sqlQuery)) {
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
