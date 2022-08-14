package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;

public class BigtableSettingsProviderImpl implements BigtableSettingsProvider {

    private final CredentialsProvider credentialsProvider;

    @Inject
    public BigtableSettingsProviderImpl(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public BigtableTableAdminSettings getTableAdminSettings(@NotNull BigtableInstance instance) throws IOException {
        return BigtableTableAdminSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .setProjectId(instance.getProjectId())
                .setInstanceId(instance.getInstanceId())
                .build();
    }

    @Override
    public BigtableDataSettings getDataSettings(@NotNull BigtableInstance instance) {
        return BigtableDataSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .setProjectId(instance.getProjectId())
                .setInstanceId(instance.getInstanceId())
                .build();
    }
}
