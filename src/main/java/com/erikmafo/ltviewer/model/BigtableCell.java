package com.erikmafo.ltviewer.model;

import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

/**
 * A simple representation of a bigtable row cell.
 */
public class BigtableCell {

    private final String family;
    private final String qualifier;
    private final ByteString bytes;
    private final long timestamp;

    /**
     * Converts a {@link RowCell} into the equivalent {@link BigtableCell} representation.
     * @param cell a bigtable row cell.
     * @return a BigtableCell.
     */
    @NotNull
    @Contract("_ -> new")
    public static BigtableCell from(@NotNull RowCell cell) {
        return new BigtableCell(
                cell.getFamily(),
                cell.getQualifier().toStringUtf8(),
                cell.getValue(),
                cell.getTimestamp());
    }

    /**
     * Creates a new instance of BigtableCell.
     *
     * @param family the name of the family that the cell belongs to.
     * @param qualifier the name of the qualifier that the cell belongs to (i.e. utf8-value of the column qualifier).
     * @param bytes the cell value.
     * @param timestamp the cell timestamp.
     */
    public BigtableCell(String family, String qualifier, ByteString bytes, long timestamp) {
        this.family = family;
        this.qualifier = qualifier;
        this.bytes = bytes;
        this.timestamp = timestamp;
    }

    /**
     * Returns the name of the family that the cell belongs to.
     *
     * @return the family that the cell belongs to.
     */
    public String getFamily() {
        return family;
    }

    /**
     * Returns the name of the column qualifier that the cell belongs to.
     *
     * @return the qualifier that the cell belongs to.
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Converts the cell value into a string by decoding the bytes as UTF-8.
     *
     * @return an UTF-8 string
     */
    public String getValueAsString() { return bytes.toStringUtf8(); }

    public String getValueAsStringBase64() { return Base64.getEncoder().encodeToString(bytes.toByteArray()); }

    /**
     * Gets the value of the cell as a byte array.
     *
     * @return a byte array.
     */
    public byte[] getBytes() {
        return bytes.toByteArray();
    }

    public ByteString getByteString() {
        return bytes;
    }

    /**
     * Gets the cell timestamp as a long.
     *
     * @return a long value representing the cell timestamp.
     */
    public long getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BigtableCell that = (BigtableCell) o;

        if (timestamp != that.timestamp) return false;
        if (!family.equals(that.family)) return false;
        if (!qualifier.equals(that.qualifier)) return false;
        return bytes.equals(that.bytes);
    }

    @Override
    public int hashCode() {
        int result = family.hashCode();
        result = 31 * result + qualifier.hashCode();
        result = 31 * result + bytes.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
