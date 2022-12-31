package com.erikmafo.btviewer.sql.query;

import com.erikmafo.btviewer.util.Check;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record Field(String name) {

    public Field {
        Check.notNullOrEmpty(name, "name");
    }

    public boolean isRowKey() {
        return name.equalsIgnoreCase("KEY");
    }

    public boolean isTimestamp() {
        return name.equalsIgnoreCase("TIMESTAMP");
    }

    public boolean isAsterisk() {
        return name.equals("*");
    }

    public String getFamily() {
        return getFamilyAndQualifier()[0];
    }

    public void ensureHasFamilyAndQualifier(String message) {
        if (getFamilyAndQualifier().length != 2) {
            throw new IllegalArgumentException(message);
        }
    }

    public boolean hasQualifier() {
        return getFamilyAndQualifier().length > 1;
    }

    @Nullable
    @Contract(pure = true)
    public String getQualifier() {
        var parts = getFamilyAndQualifier();
        if (parts.length != 2) {
            return null;
        }
        return parts[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name);
    }

    @NotNull
    @Contract(pure = true)
    private String[] getFamilyAndQualifier() {
        return name.split("\\.");
    }
}
