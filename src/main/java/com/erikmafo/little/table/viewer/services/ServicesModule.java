package com.erikmafo.little.table.viewer.services;

import com.erikmafo.little.table.viewer.config.AppConfig;
import com.erikmafo.little.table.viewer.config.ApplicationEnvironment;
import com.erikmafo.little.table.viewer.services.internal.AppDataStorage;
import com.erikmafo.little.table.viewer.services.internal.AppDataStorageImpl;
import com.erikmafo.little.table.viewer.services.internal.BigtableEmulatorSettingsProvider;
import com.erikmafo.little.table.viewer.services.internal.BigtableSettingsProvider;
import com.erikmafo.little.table.viewer.services.internal.BigtableSettingsProviderImpl;
import com.erikmafo.little.table.viewer.services.internal.DynamicCredentialsProvider;
import com.erikmafo.little.table.viewer.services.internal.TestDataUtil;
import com.google.api.gax.core.CredentialsProvider;
import com.google.inject.AbstractModule;

public class ServicesModule extends AbstractModule {

    private final AppConfig config;

    public ServicesModule() {
        this(AppConfig.load(ApplicationEnvironment.get()));
    }

    public ServicesModule(AppConfig config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        if (config.useBigtableEmulator()) {
            var emulatorSettingsProvider = new BigtableEmulatorSettingsProvider();
            emulatorSettingsProvider.startEmulator();
            TestDataUtil.injectWithTestData(emulatorSettingsProvider);
            bind(BigtableSettingsProvider.class).toInstance(emulatorSettingsProvider);
        }
        else {
            bind(BigtableSettingsProvider.class).to(BigtableSettingsProviderImpl.class);
        }

        bind(CredentialsProvider.class).to(DynamicCredentialsProvider.class);

        if (config.useInMemoryDatabase()) {
            bind(AppDataStorage.class).toInstance(AppDataStorageImpl.createInMemory());
        }
        else {
            bind(AppDataStorage.class).toInstance(AppDataStorageImpl.createInstance());
        }
    }
}
