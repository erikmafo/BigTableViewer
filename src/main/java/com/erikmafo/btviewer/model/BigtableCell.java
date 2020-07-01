package com.erikmafo.btviewer.model;

import com.google.protobuf.ByteString;

public class BigtableCell {

    private final String family;
    private final String qualifier;
    private final ByteString bytes;
    private final long timestamp;

    public BigtableCell(String family, String qualifier, ByteString bytes, long timestamp) {
        this.family = family;
        this.qualifier = qualifier;
        this.bytes = bytes;
        this.timestamp = timestamp;
    }

    public String getFamily() {
        return family;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getValueAsString() { return bytes.toStringUtf8(); }

    public byte[] getBytes() {
        return bytes.toByteArray();
    }

    public long getTimestamp() { return timestamp; }
}
