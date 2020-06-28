package com.erikmafo.btviewer.services.internal.inmemory;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableInstanceManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryInstanceManager implements BigtableInstanceManager {

    private final Object mutex = new Object();
    private List<BigtableInstance> instances;

    @Override
    public List<BigtableInstance> getInstances() {
        synchronized (mutex) {
            return new ArrayList<>(instances);
        }
    }

    @Override
    public void setInstances(List<BigtableInstance> instances) {
        synchronized (mutex) {
            this.instances = new ArrayList<>();
            this.instances.addAll(instances);
        }
    }
}
