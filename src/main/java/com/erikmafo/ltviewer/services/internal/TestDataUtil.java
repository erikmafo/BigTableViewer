package com.erikmafo.ltviewer.services.internal;

import com.erikmafo.ltviewer.model.BigtableInstance;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.erikmafo.ltviewer.util.ByteStringConverterUtil.toByteString;

public class TestDataUtil {

    private static final String JSON_TEST_DATA = "{\n"
            + "    firstName:\"fred\",\n"
            + "    hobbies:[\n"
            + "        {\n"
            + "            name:\"guitar\",\n"
            + "            tags:[\"music\",\"instrument\"]\n"
            + "        },\n"
            + "        {\n"
            + "            name:\"math\",\n"
            + "            tags:[\"science\"]\n"
            + "        },\n"
            + "    ]\n"
            + "}";

    private static final String PROJECT_0 = "project-0";
    private static final String INSTANCE_0 = "instance-0";
    private static final String TABLE_0 = "table-0";

    public static void injectWithTestData(BigtableEmulatorSettingsProvider settingsProvider) {
        try {
            createTableWithTestData(settingsProvider, new BigtableInstance(PROJECT_0, INSTANCE_0), TABLE_0);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start emulator", e);
        }
    }

    private static void createTableWithTestData(@NotNull BigtableEmulatorSettingsProvider settingsProvider, BigtableInstance instance, String tableName) throws IOException {
        var tableAdminSettings = settingsProvider.getTableAdminSettings(instance);
        try(var adminClient = BigtableTableAdminClient.create(tableAdminSettings)) {
            adminClient.createTable(CreateTableRequest.of(tableName)
                    .addFamily("f1")
                    .addFamily("f2")
                    .addFamily("f3"));
            var dataSettings = settingsProvider.getDataSettings(instance);
            addData(tableName, dataSettings);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            addData(tableName, dataSettings);
        }
    }

    private static void addData(String tableName, com.google.cloud.bigtable.data.v2.BigtableDataSettings dataSettings) throws IOException {
        try(var dataClient = BigtableDataClient.create(dataSettings)) {
            for (int i = 0; i < 1000; i++) {
                var rowKey = "row-000000000000000" + String.format("%04d", i);
                var mutation = RowMutation
                        .create(tableName, rowKey)
                        .setCell("f1", "q1", "string-" + i)
                        .setCell("f1", toByteString("q2"), toByteString(i))
                        .setCell("f1", toByteString("q3"), toByteString(i + 0.5))
                        .setCell("f1", toByteString("q4"), toByteString(JSON_TEST_DATA))
                        .setCell("f2", toByteString("q1"), toByteString("string-" + i))
                        .setCell("f3", toByteString("q1"), toByteString("string-" + i));
                dataClient.mutateRow(mutation);
            }
        }
    }
}
