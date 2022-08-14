package com.erikmafo.btviewer.ui.dialogs;

public interface DialogController<TResultValue> {

    void setResult(TResultValue value);

    TResultValue getResult();

    boolean validateResult(TResultValue value);
}
