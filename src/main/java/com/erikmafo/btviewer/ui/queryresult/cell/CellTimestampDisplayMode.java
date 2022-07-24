package com.erikmafo.btviewer.ui.queryresult.cell;

import org.jetbrains.annotations.NotNull;

public enum CellTimestampDisplayMode {

    NONE("None"),
    DATE_TIME("DateTime"),
    MILLIS("Millis"),
    MICROS("Micros");

    private final String displayValue;

    CellTimestampDisplayMode(String displayValue) {
        this.displayValue = displayValue;
    }

    @NotNull
    public String getDisplayValue() {
        return displayValue;
    }
}
