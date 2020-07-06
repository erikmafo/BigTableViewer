package com.erikmafo.btviewer.events;

import javafx.event.Event;
import javafx.event.EventType;

public class ExecuteQueryAction extends Event {

    public static final EventType<ScanTableAction> EXECUTE_QUERY_ACTION = new EventType<>(EventType.ROOT, "ExecuteQueryAction");

    private final String sql;

    public ExecuteQueryAction(String sql) {
        super(EXECUTE_QUERY_ACTION);
        this.sql = sql;
    }

    public String getSql() { return sql; }
}
