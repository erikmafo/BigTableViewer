package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class BigtableInstanceManager {

    private static final Type LIST_TYPE = new TypeToken<List<BigtableInstance>>() {}.getType();
    private static final String PREFERENCES_USER_ROOT_NODE_NAME = "bigtable-viewer-configs";
    private static final String INSTANCES = "instances";
    private final Object mutex = new Object();

    private final Gson gson = new Gson();
    private List<BigtableInstance> bigtableInstances;

    public BigtableInstanceManager() {
        String json = getPreferences().get(INSTANCES, null);
        if (json != null) {
            bigtableInstances = gson.fromJson(json, LIST_TYPE);
        }
        else {
            bigtableInstances = new ArrayList<>();
        }
    }

    public List<BigtableInstance> getInstances() {
        synchronized (mutex) {
            return bigtableInstances;
        }
    }

    public void setInstances(List<BigtableInstance> instances) {

        synchronized (mutex) {
            this.bigtableInstances = instances;
            String json = gson.toJson(instances, LIST_TYPE);
            getPreferences().put(INSTANCES, json);
        }
    }

    private Preferences getPreferences() {
        return Preferences.userRoot().node(PREFERENCES_USER_ROOT_NODE_NAME);
    }
}
