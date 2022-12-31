package com.erikmafo.btviewer.sql.query;

public class SqlQueryUtil {
    public static String getDefaultSqlQuery(String tableName) {
        return String.format("SELECT * FROM '%s' LIMIT 1000", tableName);
    }
}
