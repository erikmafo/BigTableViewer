package com.erikmafo.btviewer.services.internal.testdata;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableEmulatorSettingsProvider;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

import static com.erikmafo.btviewer.util.ByteStringConverterUtil.toByteString;

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
    public static final ByteString BYTE_STRING_TEST_VALUE = toByteString(842098349384L);

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
                    .addFamily("f3")
                    .addFamily("f4"));
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
                        .setCell("f1", "string-col", "string-" + i)
                        .setCell("f1", toByteString("int-col"), toByteString(i))
                        .setCell("f1", toByteString("double-col"), toByteString(i + 0.5))
                        .setCell("f1", toByteString("json-col"), toByteString(JSON_TEST_DATA))
                        .setCell("f2", toByteString("string-col"), toByteString("string-" + i))
                        .setCell("f3", toByteString("bytes-col"), BYTE_STRING_TEST_VALUE)
                        .setCell("f3", toByteString("uuid-col"),toByteString(UUID.randomUUID()))
                        .setCell("f4", toByteString("proto-col"), getPerson(i).toByteString());
                dataClient.mutateRow(mutation);
            }
        }
    }

    private static PersonOuterClass.Person getPerson(int i) {
        return PersonOuterClass.Person
                .newBuilder()
                .setName("Person-" + i)
                .setId("" + i)
                .setAge(i % 100)
                .setBytes(BYTE_STRING_TEST_VALUE)
                .build();
    }
}
