package com.erikmafo.btviewer;


import com.erikmafo.btviewer.services.BigtableClient;
import com.erikmafo.btviewer.services.UserConfigurationService;
import com.google.inject.AbstractModule;

public class AppConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(BigtableClient.class).toInstance(new BigtableClient());
        bind(UserConfigurationService.class).toInstance(new UserConfigurationService());
    }

}
