package com.erikmafo.btviewer.model;

public class BigtableInstance {

    private String instanceId;

    private String projectId;

    public BigtableInstance(String name)
    {
        String[] parts = name.split("/");
        this.projectId = parts[1];
        this.instanceId = parts[3];
    }

    public BigtableInstance(String projectId, String instanceId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
    }


    public String getProjectId() {
        return projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
