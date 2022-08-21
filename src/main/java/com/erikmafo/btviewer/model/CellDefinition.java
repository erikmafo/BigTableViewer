package com.erikmafo.btviewer.model;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Specifies how the value of a cell should be interpreted by assigning a value type.
 */
public class CellDefinition {

    private String valueType;
    private String family;
    private String qualifier;

    private ProtoObjectDefinition protoObjectDefinition;

    /**
     * Creates a new instance of {@code CellDefinition}.
     *
     * @param valueType             the value type of the cell.
     * @param family                the name of the column family that the cell belongs to.
     * @param qualifier             the name of the column qualifier that the cell belong to.
     * @param protoObjectDefinition specifies how the proto object should be interpreted if value type is {@link ValueTypeConstants#PROTO}
     */
    public CellDefinition(String valueType, String family, String qualifier, ProtoObjectDefinition protoObjectDefinition) {
        this.valueType = valueType;
        this.family = family;
        this.qualifier = qualifier;
        this.protoObjectDefinition = protoObjectDefinition;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public ProtoObjectDefinition getProtoObjectDefinition() {
        return protoObjectDefinition;
    }

    public void setProtoObjectDefinition(ProtoObjectDefinition protoObjectDefinition) {
        this.protoObjectDefinition = protoObjectDefinition;
    }

    public boolean matchesExact(@NotNull BigtableColumn column) {
        return column.getFamily().equals(family) && column.getQualifier().equals(qualifier);
    }

    public boolean matches(@NotNull BigtableColumn column) {
        return matches(column.getFamily(), column.getQualifier());
    }

    public boolean matches(String family, String qualifier) {
        if (!Objects.equals(family, this.family)) {
            return false;
        }

        return getQualifierPattern().matcher(qualifier).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellDefinition that = (CellDefinition) o;
        return Objects.equals(valueType, that.valueType) &&
                Objects.equals(family, that.family) &&
                Objects.equals(qualifier, that.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueType, family, qualifier);
    }

    @Override
    public String toString() {
        return "CellDefinition{" +
                "valueType='" + valueType + '\'' +
                ", family='" + family + '\'' +
                ", qualifier='" + qualifier + '\'' +
                '}';
    }

    public boolean isValid() {
        return isValidQualifierPatter() && isValidValueType();
    }

    private boolean isValidValueType() {
        if (ValueTypeConstants.PROTO.equals(valueType.toUpperCase())) {
            return protoObjectDefinition != null;
        }

        return true;
    }

    private boolean isValidQualifierPatter() {
        try {
            getQualifierPattern();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @NotNull
    private Pattern getQualifierPattern() {
        return Pattern.compile(qualifier);
    }
}
