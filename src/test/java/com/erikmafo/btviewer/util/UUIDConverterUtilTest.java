package com.erikmafo.btviewer.util;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.Assert.*;

public class UUIDConverterUtilTest {

    @Test
    public void convertUUIDToBytesReturnsMostSignificantBitsThenLeastSignificantBits() {
        // given
        var uuid = UUID.randomUUID();

        // when
        var bytes = UUIDConverterUtil.convertUUIDToBytes(uuid);

        // then
        var buffer = ByteBuffer.wrap(bytes);
        assertEquals(uuid.getMostSignificantBits(), buffer.getLong());
        assertEquals(uuid.getLeastSignificantBits(), buffer.getLong());
    }

    @Test
    public void convertBytesToUUIDReturnsOriginalUUID() {
        // given
        var uuid = UUID.randomUUID();
        var bytes = UUIDConverterUtil.convertUUIDToBytes(uuid);

        // when
        var convertedUUID = UUIDConverterUtil.convertBytesToUUID(bytes);

        // then
        assertEquals(uuid, convertedUUID);
    }
}