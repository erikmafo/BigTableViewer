package com.erikmafo.btviewer.services.internal.inmemory;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableInstanceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryInstanceManager implements BigtableInstanceManager {

    private final Object mutex = new Object();
    private List<BigtableInstance> instances;

    @Override
    public List<BigtableInstance> getInstances(String projectId) {
        synchronized (mutex) {
            return instances.stream()
                    .filter(i -> projectId.equals(i.getProjectId()))
                    .collect(Collectors.toList());
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
