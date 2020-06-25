package com.erikmafo.btviewer.config;

public class ApplicationEnvironment {

    private static final String DEVELOPMENT = "development";
    private static final String PRODUCTION = "production";

    public static ApplicationEnvironment get() {

        var env = System.getenv("APPLICATION_ENVIRONMENT");

        if (env == null || env.isEmpty()) {
            env = PRODUCTION;
        }

        return new ApplicationEnvironment(env.toLowerCase());
    }

    private final String name;

    public ApplicationEnvironment(String name) {
        this.name = name;
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
