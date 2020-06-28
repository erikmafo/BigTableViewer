package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BigtableInstanceManagerImpl implements BigtableInstanceManager {

    private static final Type LIST_TYPE = new TypeToken<List<BigtableInstance>>() {}.getType();
    private static final String INSTANCES = "instances";
    private final Object mutex = new Object();
    private final Gson gson = new Gson();
    private List<BigtableInstance> bigtableInstances;

    public BigtableInstanceManagerImpl() {

    }

    @Override
    public List<BigtableInstance> getInstances() throws IOException {
        synchronized (mutex) {

            if (bigtableInstances != null) {
                return bigtableInstances;
            }

            String json = null;

            if (Files.exists(getConfigurationFile()))
            {
                json = Files.readString(getConfigurationFile());
            }

            if (json != null) {
                bigtableInstances = gson.fromJson(json, LIST_TYPE);
            } else {
                bigtableInstances = new ArrayList<>();
            }

            return bigtableInstances;
        }
    }

    @Override
    public void setInstances(List<BigtableInstance> instances) throws IOException {

        synchronized (mutex) {
            this.bigtableInstances = instances;
            var file = getConfigurationFile();
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
            }
            String json = gson.toJson(instances, LIST_TYPE);
            Files.writeString(getConfigurationFile(), json);
        }
    }

    private Path getConfigurationFile() {
        return AppDataUtil
                .getStorageFolder()
                .resolve(INSTANCES);
    }
}

