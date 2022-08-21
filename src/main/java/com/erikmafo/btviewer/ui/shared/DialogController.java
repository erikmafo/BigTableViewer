package com.erikmafo.btviewer.ui.shared;

public interface DialogController<T> {

    void setInitialValue(T value);

    T getResult();

    default boolean validateResult(T value) {
        return true;
    }
}
