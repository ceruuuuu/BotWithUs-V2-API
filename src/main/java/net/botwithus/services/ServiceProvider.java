package net.botwithus.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public final class ServiceProvider {
    private static final Map<Class<?>, Object> INSTANCES = new HashMap<>();

    public static <T> Optional<T> provide(Class<T> clazz) {
        if(INSTANCES.containsKey(clazz)) {
            return Optional.of(clazz.cast(INSTANCES.get(clazz)));
        }
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        for (T value : loader) {
            Module module = value.getClass().getModule();
            if(module.getName().equals("BotWithUs.internal")) {
                INSTANCES.put(clazz, value);
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
