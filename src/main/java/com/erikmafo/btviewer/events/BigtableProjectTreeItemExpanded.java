package com.erikmafo.btviewer.events;

import com.erikmafo.btviewer.model.BigtableInstance;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

public class BigtableProjectTreeItemExpanded extends Event {

    public static final EventType<BigtableProjectTreeItemExpanded> PROJECT_TREE_ITEM_EXPANDED_EVENT_TYPE = new EventType<>(EventType.ROOT);

    private final List<BigtableInstance> bigtableInstances;

    public BigtableProjectTreeItemExpanded(List<BigtableInstance> instances) {
        super(PROJECT_TREE_ITEM_EXPANDED_EVENT_TYPE);
        bigtableInstances = instances;
    }

    public List<BigtableInstance> getBigtableInstances() {
        return bigtableInstances;
    }
}
