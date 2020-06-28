package com.erikmafo.btviewer.services.internal.inmemory;

import com.erikmafo.btviewer.model.*;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class TestDataUtil {

    private static final String PROJECT_0 = "project-0";
    private static final String INSTANCE_0 = "instance-0";
    private static final String TABLE_0 = "table-0";

    public static void injectWithTestData(BigtableEmulatorSettingsProvider settingsProvider) {
        try {
            var instance = new BigtableInstance(PROJECT_0, INSTANCE_0);
            var tableAdminSettings = settingsProvider.getTableAdminSettings(instance);
            var adminClient = BigtableTableAdminClient.create(tableAdminSettings);
            adminClient.createTable(CreateTableRequest
                    .of(TABLE_0)
                    .addFamily("f1")
                    .addFamily("f2")
                    .addFamily("f3"));
            adminClient.close();

            var dataSettings = settingsProvider.getDataSettings(instance);
            var dataClient = BigtableDataClient.create(dataSettings);
            injectWithTestData(dataClient);
            dataClient.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start emulator", e);
        }
    }

    public static void injectWithTestData(InMemoryInstanceManager instanceManager) {
        var instance = new BigtableInstance(PROJECT_0, INSTANCE_0);
        instanceManager.setInstances(Arrays.asList(instance));
    }

    public static void injectWithTestData(InMemoryTableConfigManager configManager) {
        var table = new BigtableTable(PROJECT_0, INSTANCE_0, TABLE_0);
        var config = new BigtableTableConfiguration();
        configManager.saveTableConfiguration(table, config);
    }

    private static void injectWithTestData(BigtableDataClient client) {
        for (int i = 0; i <1000; i++) {
            var rowKey = "row-" + String.format("%04d", i);
            var mutation = RowMutation
                    .create(TABLE_0, rowKey)
                    .setCell("f1", "q1", "string-" + i)
                    .setCell("f1", toByteString("q2"), toByteString(i))
                    .setCell("f1", toByteString("q3"), toByteString(i + 0.5));
            client.mutateRow(mutation);
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
