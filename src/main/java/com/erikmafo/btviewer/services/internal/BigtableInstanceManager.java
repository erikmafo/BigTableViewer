package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;

import java.io.IOException;
import java.util.List;

public interface BigtableInstanceManager {
    List<BigtableInstance> getInstances() throws IOException;

    void setInstances(List<BigtableInstance> instances) throws IOException;
}
