package com.erikmafo.btviewer.model;

import java.util.Objects;

public class BigtableInstance {

    private String instanceId;

    private String projectId;

    public BigtableInstance() {}

    public BigtableInstance(BigtableInstance instance) {
        this.projectId = instance.getProjectId();
        this.instanceId = instance.getInstanceId();
    }

    public BigtableInstance(String projectId, String instanceId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigtableInstance that = (BigtableInstance) o;
        return Objects.equals(instanceId, that.instanceId) &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, projectId);
    }
}
