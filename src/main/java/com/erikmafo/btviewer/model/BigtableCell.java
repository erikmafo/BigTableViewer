package com.erikmafo.btviewer.model;

import com.google.bigtable.repackaged.com.google.protobuf.ByteString;

public class BigtableCell {

    private final String family;
    private final String qualifier;
    private final ByteString bytes;

    public BigtableCell(String family, String qualifier, ByteString bytes) {
        this.family = family;
        this.qualifier = qualifier;
        this.bytes = bytes;
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
}
