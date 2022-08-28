package com.erikmafo.btviewer.ui.queryresult.cell;

import org.jetbrains.annotations.NotNull;

public enum CellTimestampDisplayMode {

    NONE("Hide timestamps"),
    DATE_TIME("Timestamps as date time"),
    MILLIS("Timestamps as millis"),
    MICROS("Timestamps as micros");

    private final String displayValue;

    CellTimestampDisplayMode(String displayValue) {
        this.displayValue = displayValue;
    }

    @NotNull
    public String getDisplayValue() {
        return displayValue;
    }
}
