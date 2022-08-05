package com.erikmafo.ltviewer.services.internal;

import com.erikmafo.ltviewer.model.BigtableInstance;
import com.erikmafo.ltviewer.model.BigtableTable;
import com.erikmafo.ltviewer.model.BigtableTableSettings;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Singleton
public class AppDataStorageImpl implements AppDataStorage {

    public static final String INSTANCES = "instances";
    public static final String TABLE_SETTINGS = "table-settings";

    private final DB database;
    private final ConcurrentMap<String, BigtableInstance> instances;
    private final ConcurrentMap<String, BigtableTableSettings> tableSettings;

    @NotNull
    public static AppDataStorage createInMemory() {
        var database = DBMaker
                .memoryDB()
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
        return new AppDataStorageImpl(database);
    }

    @NotNull
    public static AppDataStorage createInstance() {
        var storageDir = AppDataUtil.getStorageFolder();
        if (!Files.exists(storageDir)) {
            try {
                Files.createDirectories(storageDir);
            } catch (IOException e) {
                return createInMemory();
            }
        }

        var dbFile = storageDir.resolve("database").toFile();
        var database = DBMaker
                .fileDB(dbFile)
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
        return new AppDataStorageImpl(database);
    }

    private static class JsonSerializer<T> implements Serializer<T> {
        private final Class<T> clazz;
        private final Gson gson = new Gson();

        public JsonSerializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void serialize(@NotNull DataOutput2 output, @NotNull T t) throws IOException {
            output.writeUTF(gson.toJson(t));
        }

        @Override
        public T deserialize(@NotNull DataInput2 input, int i) throws IOException {
            return gson.fromJson(input.readUTF(), clazz);
        }
    }

    public AppDataStorageImpl(DB database) {
        this.database = database;
        this.instances = openOrCreateInstances();
        this.tableSettings = openOrCreateTableSettings();
    }

    @Override
    public List<String> getProjects() {
        return openOrCreateInstances()
                .values()
                .stream()
                .map(BigtableInstance::getProjectId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void removeProject(String projectId) {
        for (var instance : getInstances(projectId)) {
            removeInstanceNoCommit(instance);
        }
        database.commit();
    }

    @Override
    public List<BigtableInstance> getInstances(String projectId) {
        return openOrCreateInstances()
                .values()
                .stream()
                .filter(instance -> instance.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public void addInstance(BigtableInstance instance) {
        openOrCreateInstances().put(getKey(instance), instance);
        database.commit();
    }

    @Override
    public void removeInstance(BigtableInstance instance) {
        removeInstanceNoCommit(instance);
        database.commit();
    }

    @Override
    public BigtableTableSettings getTableSettings(BigtableTable table) {
        return this.openOrCreateTableSettings().getOrDefault(getKey(table), new BigtableTableSettings());
    }

    @Override
    public void saveTableConfiguration(BigtableTable table, BigtableTableSettings settings) {
        this.openOrCreateTableSettings().put(getKey(table), settings);
        this.database.commit();
    }

    @NotNull
    private String getKey(@NotNull BigtableInstance instance) {
        return String.format("projects/%s/instances/%s", instance.getProjectId(), instance.getInstanceId());
    }

    @NotNull
    private String getKey(@NotNull BigtableTable table) {
        return String.format("projects/%s/instances/%s/tables/%s", table.getProjectId(), table.getInstanceId(), table.getTableId());
    }

    @NotNull
    private ConcurrentMap<String, BigtableTableSettings> openOrCreateTableSettings() {
        return this.database
                .hashMap(TABLE_SETTINGS)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new JsonSerializer<>(BigtableTableSettings.class))
                .createOrOpen();
    }

    @NotNull
    private ConcurrentMap<String, BigtableInstance> openOrCreateInstances() {
        return this.database
                .hashMap(INSTANCES)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new JsonSerializer<>(BigtableInstance.class))
                .createOrOpen();
    }

    private void removeInstanceNoCommit(BigtableInstance instance) {
        var instanceKey = getKey(instance);
        instances.remove(instanceKey);
        var tablesToRemove = tableSettings.keySet()
                .stream()
                .filter(key -> key.startsWith(String.format("%s/tables/", instanceKey)))
                .collect(Collectors.toList());
        for (var tableKey : tablesToRemove) {
            tableSettings.remove(tableKey);
        }
    }
}



