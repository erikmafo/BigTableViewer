package com.erikmafo.btviewer.events;
import javafx.event.Event;
import javafx.event.EventType;

public class ScanTableAction extends Event {

    public static final EventType<ScanTableAction> SCAN_TABLE = new EventType<>(EventType.ROOT, "ScanTableAction");

    private final String from;
    private final String to;

    public ScanTableAction(String from, String to) {
        super(SCAN_TABLE);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
