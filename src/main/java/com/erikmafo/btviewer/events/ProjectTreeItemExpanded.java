package com.erikmafo.btviewer.events;

import com.erikmafo.btviewer.model.BigtableInstance;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class ProjectTreeItemExpanded extends Event {

    public static final EventType<ProjectTreeItemExpanded> PROJECT_TREE_ITEM_EXPANDED_EVENT_TYPE = new EventType<>(EventType.ROOT, "ProjectTreeItemExpanded");

    private final String projectId;
    private final List<BigtableInstance> instances;

    public ProjectTreeItemExpanded(String projectId, List<BigtableInstance> instances) {
        super(PROJECT_TREE_ITEM_EXPANDED_EVENT_TYPE);
        this.projectId = projectId;
        this.instances = instances;
    }

    public String getProjectId() {
        return projectId;
    }

    public List<BigtableInstance> getInstances() {
        return instances;
    }
}
