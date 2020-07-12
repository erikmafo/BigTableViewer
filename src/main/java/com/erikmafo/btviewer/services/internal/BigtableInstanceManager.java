package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;

import java.io.IOException;
import java.util.List;

public interface BigtableInstanceManager {

    List<String> getProjects() throws IOException;

    void removeProject(String projectId) throws IOException;

    List<BigtableInstance> getInstances(String projectId) throws IOException;

    void addInstance(BigtableInstance instance) throws IOException;

    void removeInstance(BigtableInstance instance) throws IOException;
}
