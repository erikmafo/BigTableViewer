package com.erikmafo.btviewer.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDConverterUtil {

    @NotNull
    public static byte[] convertUUIDToBytes(@NotNull UUID uuid) {
        var buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    @NotNull
    public static UUID convertBytesToUUID(byte[] bytes) {
        var buffer = ByteBuffer.wrap(bytes);
        var mostSignificantBits = buffer.getLong();
        var leastSignificantBits = buffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
