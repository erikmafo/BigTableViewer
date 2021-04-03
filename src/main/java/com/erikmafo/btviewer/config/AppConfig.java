package com.erikmafo.btviewer.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AppConfig {

    private boolean useBigtableEmulator;
    private boolean useInMemoryDatabase;

    public AppConfig() {}

    public static AppConfig load(ApplicationEnvironment environment) {
        return ConfigInjectionUtil.loadConfigProperties(getConfigName(environment), AppConfig.class);
    }

    @Inject
    public AppConfig(@Named("USE_BIGTABLE_EMULATOR") boolean useBigtableEmulator,
                     @Named("USE_IN_MEMORY_DATABASE") boolean useInMemoryDatabase) {
        this.useBigtableEmulator = useBigtableEmulator;
        this.useInMemoryDatabase = useInMemoryDatabase;
    }

    public boolean useBigtableEmulator() { return useBigtableEmulator; }

    @VisibleForTesting
    public void setUseBigtableEmulator(boolean useBigtableEmulator) {
        this.useBigtableEmulator = useBigtableEmulator;
    }

    @VisibleForTesting
    public boolean useInMemoryDatabase() {
        return useInMemoryDatabase;
    }

    public void setUseInMemoryDatabase(boolean useInMemoryDatabase) {
        this.useInMemoryDatabase = useInMemoryDatabase;
    }

    private static String getConfigName(ApplicationEnvironment environment) {
        return environment.isProduction() ?
                "config.properties" :
                String.format("config.%s.properties", environment.getName());
    }
}
