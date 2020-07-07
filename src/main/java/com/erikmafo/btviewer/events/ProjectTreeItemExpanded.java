package com.erikmafo.btviewer.events;

import com.erikmafo.btviewer.model.BigtableInstance;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class ProjectTreeItemExpanded extends Event {

    public static final EventType<ProjectTreeItemExpanded> PROJECT_TREE_ITEM_EXPANDED_EVENT_TYPE = new EventType<>(EventType.ROOT, "ProjectTreeItemExpanded");

    private final List<BigtableInstance> bigtableInstances;

    public ProjectTreeItemExpanded(List<BigtableInstance> instances) {
        super(PROJECT_TREE_ITEM_EXPANDED_EVENT_TYPE);
        bigtableInstances = instances;
    }

    public List<BigtableInstance> getInstances() {
        return bigtableInstances;
    }
}
