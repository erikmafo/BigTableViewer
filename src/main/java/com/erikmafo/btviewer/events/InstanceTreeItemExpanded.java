package com.erikmafo.btviewer.events;

import com.erikmafo.btviewer.model.BigtableInstance;
import javafx.event.Event;
import javafx.event.EventType;

public class InstanceTreeItemExpanded extends Event {
    public static final EventType<ProjectTreeItemExpanded> INSTANCE_TREE_ITEM_EXPANDED_EVENT_TYPE = new EventType<>(EventType.ROOT, "InstanceTreeItemExpanded");

    private final BigtableInstance bigtableInstance;

    public InstanceTreeItemExpanded(BigtableInstance instance) {
        super(INSTANCE_TREE_ITEM_EXPANDED_EVENT_TYPE);
        bigtableInstance = instance;
    }

    public BigtableInstance getInstance() {
        return bigtableInstance;
    }
}
