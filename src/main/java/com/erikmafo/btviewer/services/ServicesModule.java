package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.config.AppConfig;
import com.erikmafo.btviewer.config.ApplicationEnvironment;
import com.erikmafo.btviewer.services.internal.inmemory.BigtableEmulatorSettingsProvider;
import com.erikmafo.btviewer.services.internal.inmemory.InMemoryInstanceManager;
import com.erikmafo.btviewer.services.internal.inmemory.InMemoryTableConfigManager;
import com.erikmafo.btviewer.services.internal.inmemory.TestDataUtil;
import com.erikmafo.btviewer.services.internal.*;
import com.google.api.gax.core.CredentialsProvider;
import com.google.inject.*;
import com.google.inject.Module;

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

        if (config.useInMemoryTableConfigManager()) {
            var inMemoryTableConfigManager = new InMemoryTableConfigManager();
            bind(TableConfigManager.class).toInstance(inMemoryTableConfigManager);
        }
        else {
            bind(TableConfigManager.class).toInstance(new TableConfigManagerImpl());
        }

        if (config.useInMemoryInstanceManager()) {
            var inMemoryInstanceManager = new InMemoryInstanceManager();
            TestDataUtil.injectWithTestData(inMemoryInstanceManager);
            bind(BigtableInstanceManager.class).toInstance(inMemoryInstanceManager);
        } else {
            bind(BigtableInstanceManager.class).toInstance(new BigtableInstanceManagerImpl());
        }
    }
}
