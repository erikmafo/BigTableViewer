package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.erikmafo.btviewer.services.internal.BigtableSettingsProvider;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.emulator.v2.Emulator;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BigtableEmulatorSettingsProvider implements BigtableSettingsProvider {

    private static final String LOCALHOST = "localhost";
    private Emulator emulator;

    public BigtableEmulatorSettingsProvider() {
    }

    public void startEmulator() {

        if (emulator != null) {
            throw new IllegalStateException("Emulator already started");
        }

        try {
            this.emulator = Emulator.createBundled();
            emulator.start();
        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException("Failed to start emulator", e);
        }
    }

    public void stopEmulator() {
        if (emulator != null) {
            emulator.stop();
        }
    }

    @Override
    public BigtableTableAdminSettings getTableAdminSettings(BigtableInstance instance) throws IOException {
        return BigtableTableAdminSettings
                .newBuilderForEmulator(LOCALHOST, emulator.getPort())
                .setProjectId(instance.getProjectId())
                .setInstanceId(instance.getInstanceId())
                .build();
    }

    @Override
    public BigtableDataSettings getDataSettings(BigtableInstance instance) {
        return BigtableDataSettings
                .newBuilderForEmulator(LOCALHOST, emulator.getPort())
                .setProjectId(instance.getProjectId())
                .setInstanceId(instance.getInstanceId())
                .build();
    }
}
