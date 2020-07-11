package com.erikmafo.btviewer.events;

import com.erikmafo.btviewer.sql.Query;
import javafx.event.Event;
import javafx.event.EventType;

public class ExecuteQueryAction extends Event {

    public static final EventType<ExecuteQueryAction> EXECUTE_QUERY_ACTION = new EventType<>(EventType.ROOT, "ExecuteQueryAction");

    private final Query sqlQuery;

    public ExecuteQueryAction(Query sqlQuery) {
        super(EXECUTE_QUERY_ACTION);
        this.sqlQuery = sqlQuery;
    }

    public Query getSqlQuery() { return sqlQuery; }
}
