package com.erikmafo.btviewer.util;

import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;

public class ByteStringConverterUtil {

    public static ByteString toByteString(String stringUtf8) {
        return ByteString.copyFromUtf8(stringUtf8);
    }

    public static ByteString toByteString(long value) {
        var bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(value);
        return ByteString.copyFrom(bytes);
    }

    public static ByteString toByteString(int value) {
        var bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(value);
        return ByteString.copyFrom(bytes);
    }

    public static ByteString toByteString(float value) {
        var bytes = new byte[4];
        ByteBuffer.wrap(bytes).putFloat(value);
        return ByteString.copyFrom(bytes);
    }

    public static ByteString toByteString(double value) {
        var bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return ByteString.copyFrom(bytes);
    }
}
