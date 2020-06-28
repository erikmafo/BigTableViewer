package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.config.AppConfig;
import com.erikmafo.btviewer.config.ApplicationEnvironment;
import com.erikmafo.btviewer.services.internal.inmemory.BigtableEmulatorSettingsProvider;
import com.erikmafo.btviewer.services.internal.inmemory.InMemoryInstanceManager;
import com.erikmafo.btviewer.services.internal.inmemory.InMemoryTableConfigManager;
import com.erikmafo.btviewer.services.internal.inmemory.TestDataUtil;
import com.erikmafo.btviewer.services.internal.*;
import com.google.api.gax.core.CredentialsProvider;
import com.google.inject.Binder;
import com.google.inject.Module;

public class ServicesModule implements Module {

    @Override
    public void configure(Binder binder) {
        var config = AppConfig.load(ApplicationEnvironment.get());

        if (config.useBigtableEmulator()) {
            var emulatorSettingsProvider = new BigtableEmulatorSettingsProvider();
            emulatorSettingsProvider.startEmulator();
            TestDataUtil.injectWithTestData(emulatorSettingsProvider);
            binder.bind(BigtableSettingsProvider.class).toInstance(emulatorSettingsProvider);
        }
        else {
            binder.bind(BigtableSettingsProvider.class).to(BigtableSettingsProviderImpl.class);
        }

        binder.bind(CredentialsProvider.class).to(DynamicCredentialsProvider.class);

        if (config.useInMemoryTableConfigManager()) {
            var inMemoryTableConfigManager = new InMemoryTableConfigManager();
            binder.bind(TableConfigManager.class).toInstance(inMemoryTableConfigManager);
        }
        else {
            binder.bind(TableConfigManager.class).toInstance(new TableConfigManagerImpl());
        }

        if (config.useInMemoryInstanceManager()) {
            var inMemoryInstanceManager = new InMemoryInstanceManager();
            TestDataUtil.injectWithTestData(inMemoryInstanceManager);
            binder.bind(BigtableInstanceManager.class).toInstance(inMemoryInstanceManager);
        } else {
            binder.bind(BigtableInstanceManager.class).toInstance(new BigtableInstanceManagerImpl());
        }
    }
}
