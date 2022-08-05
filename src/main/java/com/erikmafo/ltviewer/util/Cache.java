package com.erikmafo.ltviewer.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Cache {

    @FunctionalInterface
    public interface ValueProvider<T> {
        T getValue(String key) throws IOException;
    }

    private final Object mutex = new Object();
    private final Map<String, Object> map = new HashMap<>();

    public <T> T getOrCreate(String key, ValueProvider<T> valueProvider) throws IOException {
        synchronized (mutex) {
            T obj;

            synchronized (mutex) {
                if (map.containsKey(key)) {
                    obj = (T) map.get(key);
                } else {
                    obj = valueProvider.getValue(key);
                    map.put(key, obj);
                }
            }

            return obj;
        }
    }
}
