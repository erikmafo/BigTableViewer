package com.erikmafo.btviewer.ui.shared;

public interface DialogController<TResultValue> {

    void setInitialValue(TResultValue value);

    TResultValue getResult();

    default boolean validateResult(TResultValue value) {
        return true;
    }
}
