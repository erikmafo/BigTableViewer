package com.erikmafo.btviewer.util;

import com.google.protobuf.ByteString;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByteStringConverterUtilTest {

    @Test
    public void toByteString_withString_returnsUtf8ByteString() {
        // given
        var data = "test";

        // when
        var byteString = ByteStringConverterUtil.toByteString(data);

        // then
        assertEquals(ByteString.copyFromUtf8(data), byteString);
    }

    @Test
    public void toByteString_withFloatValue_returnsByteStringOfSize4() {
        // given
        float data = 42f;

        // when
        var byteString = ByteStringConverterUtil.toByteString(data);

        // then
        assertEquals(4, byteString.size());
    }

    @Test
    public void toByteString_withIntValue_returnsByteStringOfSize4() {
        // given
        int data = 42;

        // when
        var byteString = ByteStringConverterUtil.toByteString(data);

        // then
        assertEquals(4, byteString.size());
    }

    @Test
    public void toByteString_withLongValue_returnsByteStringOfSize8() {
        // given
        long data = 42L;

        // when
        var byteString = ByteStringConverterUtil.toByteString(data);

        // then
        assertEquals(8, byteString.size());
    }
}