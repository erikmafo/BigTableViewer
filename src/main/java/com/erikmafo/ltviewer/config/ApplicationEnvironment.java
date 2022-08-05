package com.erikmafo.ltviewer.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ApplicationEnvironment {

    private static final String DEVELOPMENT = "development";
    private static final String PRODUCTION = "production";

    private final String name;

    public ApplicationEnvironment(String name) {
        this.name = name;
    }

    @NotNull
    @Contract(" -> new")
    public static ApplicationEnvironment get() {

        var env = System.getenv("APPLICATION_ENVIRONMENT");

        if (env == null || env.isEmpty()) {
            env = PRODUCTION;
        }

        return new ApplicationEnvironment(env.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public boolean isDevelopment() {
        return DEVELOPMENT.equalsIgnoreCase(name);
    }

    public boolean isProduction() {
        return PRODUCTION.equalsIgnoreCase(name);
    }
}
