package com.erikmafo.btviewer.services.inmemory;

import com.erikmafo.btviewer.model.*;
import com.google.bigtable.repackaged.com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class TestDataUtil {

    private static final String PROJECT_0 = "project-0";
    private static final String INSTANCE_0 = "instance-0";
    private static final String TABLE_0 = "table-0";

    public static void injectWithTestData(InMemoryInstanceManager instanceManager) {
        var instance = new BigtableInstance(PROJECT_0, INSTANCE_0);
        instanceManager.setInstances(Arrays.asList(instance));
    }

    public static void injectWithTestData(InMemoryTableConfigManager configManager) {
        var table = new BigtableTable(PROJECT_0, INSTANCE_0, TABLE_0);
        var config = new BigtableTableConfiguration();
        configManager.saveTableConfiguration(table, config);
    }

    public static void injectWithTestData(InMemoryBigtableClient inMemoryBigtableClient) {
        var table = new BigtableTable(PROJECT_0, INSTANCE_0, TABLE_0);

        for (int i = 0; i <1000; i++) {
            var rowKey = "row-" + String.format("%04d", i);
            var cells = Arrays.asList(
                    new BigtableCell("f1", "q1", toByteString("string-" + i)),
                    new BigtableCell("f1", "q2", toByteString(i)),
                    new BigtableCell("f1", "q3", toByteString(i + 0.5)));

            inMemoryBigtableClient.addRow(table, new BigtableRow(rowKey, cells));
        }
    }

    private static ByteString toByteString(String value) {
        return ByteString.copyFromUtf8(value);
    }

    private static ByteString toByteString(int value) {
        return ByteString.copyFrom(toByteArray(value));
    }

    private static ByteString toByteString(double value) {
        return ByteString.copyFrom(toByteArray(value));
    }

    private static byte[] toByteArray(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }

    private static byte[] toByteArray(double i) {
        final ByteBuffer bb = ByteBuffer.allocate(Double.SIZE / Byte.SIZE);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putDouble(i);
        return bb.array();
    }
}
