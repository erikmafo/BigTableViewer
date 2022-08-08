package com.erikmafo.ltviewer.util;

import com.google.protobuf.ByteString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HexFormat;

public class ByteStringConverterUtil {

    @NotNull
    @Contract("_ -> new")
    public static ByteString toByteString(String stringUtf8) {
        return ByteString.copyFromUtf8(stringUtf8);
    }

    @NotNull
    @Contract("_ -> new")
    public static ByteString toByteStringFromHex(String hex) {
        return ByteString.copyFrom(HexUtil.toBytes(hex));
    }

    @NotNull
    public static ByteString toByteString(long value) {
        var buffer = ByteBuffer
                .allocate(Long.SIZE / Byte.SIZE)
                .putLong(value)
                .order(ByteOrder.BIG_ENDIAN);
        return ByteString.copyFrom(buffer.array());
    }

    @NotNull
    public static ByteString toByteString(int value) {
        var buffer = ByteBuffer
                .allocate(Integer.SIZE / Byte.SIZE)
                .putInt(value)
                .order(ByteOrder.BIG_ENDIAN);
        return ByteString.copyFrom(buffer.array());
    }

    @NotNull
    public static ByteString toByteString(float value) {
        var buffer = ByteBuffer
                .allocate(Float.SIZE / Byte.SIZE)
                .putFloat(value)
                .order(ByteOrder.BIG_ENDIAN);
        return ByteString.copyFrom(buffer.array());
    }

    @NotNull
    public static ByteString toByteString(double value) {
        var buffer = ByteBuffer
                .allocate(Double.SIZE / Byte.SIZE)
                .putDouble(value)
                .order(ByteOrder.BIG_ENDIAN);
        return ByteString.copyFrom(buffer.array());
    }
}
