package net.botwithus.scripts;

import net.botwithus.scripts.repositories.LocalRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public final class RepositoryRegistry {
    private static final Map<String, ScriptRepository> registeredRepositories = new ConcurrentHashMap<>();
    private static final Map<String, ModuleLayer> registeredLayers = new ConcurrentHashMap<>();

    private static final LocalRepository LOCAL_REPOSITORY = new LocalRepository();

    public static boolean isRegistered(String name) {
        return registeredRepositories.containsKey(name);
    }

    public static void registerRepository(String name, String moduleName, ModuleLayer layer, ScriptRepository repository) {
        registeredRepositories.put(name, repository);
        registeredLayers.put(moduleName, layer);
    }

    public static Optional<ScriptRepository> getRepository(String name) {
        if(registeredRepositories.containsKey(name)) {
            return Optional.of(registeredRepositories.get(name));
        }
        ServiceLoader<ScriptRepository> loader = ServiceLoader.load(ScriptRepository.class);
        for (ServiceLoader.Provider<ScriptRepository> provider : loader.stream().toList()) {
            Class<? extends ScriptRepository> clazz = provider.type();
            if(clazz.isAnnotationPresent(Info.class)) {
                Info info = clazz.getAnnotation(Info.class);
                if(info.name().equals(name)) {
                    ScriptRepository repo = provider.get();
                    registeredRepositories.put(name, repo);
                    return Optional.of(repo);
                }
            }
        }
        return Optional.empty();
    }

    public static boolean isRegisteredModule(String moduleName) {
        return registeredLayers.containsKey(moduleName);
    }

    public static void removeModule(String moduleName) {
        registeredLayers.remove(moduleName);
        registeredRepositories.remove(moduleName);
    }

    public static LocalRepository getLocalRepository() {
        return LOCAL_REPOSITORY;
    }

    public static Collection<ScriptRepository> getRepositories() {
        return registeredRepositories.values();
    }

    public static boolean hasOtherRepositories() {
        return !registeredRepositories.isEmpty();
    }

}
