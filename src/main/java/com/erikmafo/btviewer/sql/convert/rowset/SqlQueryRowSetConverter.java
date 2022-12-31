package com.erikmafo.btviewer.sql.convert.rowset;

import com.erikmafo.btviewer.sql.query.SqlQuery;
import com.erikmafo.btviewer.sql.query.WhereClause;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that is responsible for converting sql where clauses involving the row key into BigtableRowSet
 * (row keys or byte string range).
 */
public class SqlQueryRowSetConverter {

    public SqlQueryRowSetConverter() {}

    /**
     * Creates a BigtableRowSet that covers a part of the given sql query that involves the row key.
     * @param sqlQuery a sql query
     * @return a BigtableRowSet.
     */
    @NotNull
    public BigtableRowSet convert(@NotNull SqlQuery sqlQuery) {
        var rows = BigtableRowSet.unbounded();
        for (var whereClause : getRowKeyWhereClauses(sqlQuery)) {
            rows = updateRows(rows, whereClause);

            if (rows.isEmpty()) {
                break;
            }
        }

        return rows;
    }

    public BigtableRowSet updateRows(@NotNull BigtableRowSet rows, @NotNull WhereClause whereClause) {
        return switch (whereClause.operator()) {
            case EQUAL -> rows.withEquals(getValueAsByteString(whereClause));
            case LESS_THAN -> rows.withLessThan(getValueAsByteString(whereClause));
            case LESS_THAN_OR_EQUAL -> rows.withLessThanOrEqual(getValueAsByteString(whereClause));
            case GREATER_THAN -> rows.withGreaterThan(getValueAsByteString(whereClause));
            case GREATER_THAN_OR_EQUAL -> rows.withGreaterThanOrEqual(getValueAsByteString(whereClause));
            case IN -> rows.withEqualsAny(getValuesAsByteString(whereClause));
            default -> rows;
        };
    }

    @Contract("_ -> new")
    @NotNull
    private static ByteString getValueAsByteString(@NotNull WhereClause whereClause) {
        return whereClause.getValue().asByteStringUtf8();
    }

    @Contract("_ -> new")
    @NotNull
    private static List<ByteString> getValuesAsByteString(@NotNull WhereClause whereClause) {
        return whereClause.values().stream().map(v -> v.asByteStringUtf8()).collect(Collectors.toList());
    }

    @NotNull
    private static List<WhereClause> getRowKeyWhereClauses(@NotNull SqlQuery sqlQuery) {
        return sqlQuery
                .whereClauses()
                .stream()
                .filter(where -> where.field().isRowKey())
                .collect(Collectors.toList());
    }
}
