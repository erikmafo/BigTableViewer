package com.erikmafo.little.table.viewer.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jetbrains.annotations.NotNull;

public class AppConfig {

    @Inject @Named("USE_BIGTABLE_EMULATOR")
    private boolean useBigtableEmulator;

    @Inject @Named("USE_IN_MEMORY_DATABASE")
    private boolean useInMemoryDatabase;

    public static AppConfig load(ApplicationEnvironment environment) {
        return ConfigInjectionUtil.loadConfigProperties(getConfigName(environment), AppConfig.class);
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

    private static String getConfigName(@NotNull ApplicationEnvironment environment) {
        return environment.isProduction() ?
                "config.properties" :
                String.format("config.%s.properties", environment.getName());
    }
}
