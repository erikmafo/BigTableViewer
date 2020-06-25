package com.erikmafo.btviewer.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

import java.io.IOException;
import java.util.Properties;

class ConfigInjectionUtil {

    static <T> T loadConfigProperties(String configName, Class<T> classType) {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                var inputStream = getClass().getClassLoader().getResourceAsStream(configName);
                Properties props = new Properties();
                if (inputStream != null) {
                    try {
                        props.load(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Could not find config file " + configName);
                }

                Names.bindProperties(binder(), props);
            }
        };

        return Guice.createInjector(module).getInstance(classType);
    }
}
