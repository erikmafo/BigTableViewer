package com.erikmafo.btviewer.events;

import com.erikmafo.btviewer.sql.SqlQuery;
import javafx.event.Event;
import javafx.event.EventType;

public class ExecuteQueryAction extends Event {

    public static final EventType<ExecuteQueryAction> EXECUTE_QUERY_ACTION = new EventType<>(EventType.ROOT, "ExecuteQueryAction");

    private final SqlQuery sqlQuery;

    public ExecuteQueryAction(SqlQuery sqlQuery) {
        super(EXECUTE_QUERY_ACTION);
        this.sqlQuery = sqlQuery;
    }

    public SqlQuery getSqlQuery() { return sqlQuery; }
}
