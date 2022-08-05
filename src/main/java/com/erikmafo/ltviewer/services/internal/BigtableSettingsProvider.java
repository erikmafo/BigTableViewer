package com.erikmafo.ltviewer.services.internal;

import com.erikmafo.ltviewer.model.BigtableInstance;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;

import java.io.IOException;

public interface BigtableSettingsProvider {

    BigtableTableAdminSettings getTableAdminSettings(BigtableInstance instance) throws IOException;

    BigtableDataSettings getDataSettings(BigtableInstance instance);
}
