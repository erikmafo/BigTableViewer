package com.erikmafo.btviewer.ui.dialogs.tablesettings;

import com.erikmafo.btviewer.model.CellDefinition;
import com.erikmafo.btviewer.model.ProtoObjectDefinition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jetbrains.annotations.NotNull;

class ObservableCell {

    private final StringProperty valueType = new SimpleStringProperty();
    private final StringProperty family = new SimpleStringProperty();
    private final StringProperty qualifier = new SimpleStringProperty();
    private final ObjectProperty<ProtoObjectDefinition> protoObjectDefinition = new SimpleObjectProperty<>();


    public String getValueType() {
        return valueType.get();
    }

    @NotNull
    public StringProperty valueTypeProperty() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType.set(valueType);
    }

    public String getFamily() {
        return family.get();
    }

    @NotNull
    public StringProperty familyProperty() {
        return family;
    }

    public void setFamily(String family) {
        this.family.set(family);
    }

    public String getQualifier() {
        return qualifier.get();
    }

    @NotNull
    public StringProperty qualifierProperty() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier.set(qualifier);
    }

    public ProtoObjectDefinition getProtoObjectDefinition() {
        return protoObjectDefinition.get();
    }

    public ObjectProperty<ProtoObjectDefinition> protoObjectDefinitionProperty() {
        return protoObjectDefinition;
    }

    public void setProtoObjectDefinition(ProtoObjectDefinition protoObjectDefinition) {
        this.protoObjectDefinition.set(protoObjectDefinition);
    }

    public CellDefinition toCellDefinition() {
        return new CellDefinition(valueType.get(), family.get(), qualifier.get(), protoObjectDefinition.get());
    }
}
