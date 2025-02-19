package net.botwithus.scripts.repositories;

import net.botwithus.events.Event;
import net.botwithus.events.EventInfo;
import net.botwithus.scripts.Info;
import net.botwithus.scripts.RepositoryRegistry;
import net.botwithus.scripts.Script;
import net.botwithus.scripts.ScriptRepository;

import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LocalRepository implements ScriptRepository {

    private static final Logger log = Logger.getLogger(LocalRepository.class.getName());

    private final Map<String, Script> scripts;

    public LocalRepository() {
        this.scripts = new ConcurrentHashMap<>();
    }

    @Override
    public boolean load(URL url) {
        try {
            // Convert the URL to a Path
            Path jarPath = Path.of(url.toURI());
            // Create a ModuleFinder that locates the module in the specified path
            ModuleFinder finder = ModuleFinder.of(jarPath);
            Set<ModuleReference> moduleReferences = finder.findAll();

            if (moduleReferences.isEmpty()) {
                log.log(Level.WARNING, "No modules found in the provided JAR");
                return false;
            }

            // Dynamically resolve and load the module
            ModuleLayer bootLayer = ModuleLayer.boot();
            Configuration configuration = bootLayer.configuration()
                    .resolve(finder, ModuleFinder.of(),
                            moduleReferences.stream()
                                    .map(ModuleReference::descriptor)
                                    .map(ModuleDescriptor::name)
                                    .collect(Collectors.toSet()));

            ClassLoader loader = newClassLoader(ClassLoader.getSystemClassLoader());

            ModuleLayer layer = bootLayer.defineModulesWithOneLoader(configuration, loader);

            for (ModuleReference moduleRef : moduleReferences) {
                String moduleName = moduleRef.descriptor().name();

                // Find the module in the new layer
                layer.findModule(moduleName).ifPresentOrElse(
                        _ -> initializeServices(url, layer, moduleName),
                        () -> log.log(Level.WARNING, "Module not found: " + moduleName)
                );
            }
            return true; // Successfully loaded the module and services
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to load module", e);
            return false; // Failed to load the module
        }
    }

    private void initializeServices(URL url, ModuleLayer layer, String moduleName) {
        ServiceLoader<ScriptRepository> repositoryServices = ServiceLoader.load(layer, ScriptRepository.class);
        for (ServiceLoader.Provider<ScriptRepository> provider : repositoryServices.stream().toList()) {
            Class<? extends ScriptRepository> clazz = provider.type();
            if (!clazz.isAnnotationPresent(Info.class)) {
                continue;
            }
            Info info = clazz.getAnnotation(Info.class);
            if (RepositoryRegistry.isRegistered(moduleName)) {
                log.log(Level.WARNING, "Repository " + info.name() + " already loaded for module " + moduleName);
                continue;
            }
            RepositoryRegistry.registerRepository(info.name(), moduleName, layer, provider.get());
        }

        ServiceLoader<Script> scriptServices = ServiceLoader.load(layer, Script.class);
        for (ServiceLoader.Provider<Script> provider : scriptServices.stream().toList()) {
            Class<? extends Script> clazz = provider.type();
            if (!clazz.isAnnotationPresent(Info.class)) {
                continue;
            }
            Info info = clazz.getAnnotation(Info.class);
            if (scripts.containsKey(info.name())) {
                log.log(Level.WARNING, "Script " + info.name() + " already loaded.");
                continue;
            }
            try {
                Script script = provider.get();
                script.setRepository(this);
                script.setUrl(url);
                scripts.put(info.name(), script);
                log.log(Level.INFO, "Script [" + info.name() + "] loaded.");
            } catch (Exception e) {
                log.log(Level.WARNING, "Failed to load script", e);
            }
        }
    }

    @Override
    public boolean reload(Script script) {
        if(script.isActive()) {
            script.setActive(false);
        }
        Info info = script.getClass().getAnnotation(Info.class);
        scripts.remove(info.name());
        return load(script.getUrl());
    }

    @Override
    public void receiveEvent(Event event) {
        for (Script script : scripts.values()) {
            for (Method method : script.getClass().getDeclaredMethods()) {
                if(!method.isAnnotationPresent(EventInfo.class)) {
                    continue;
                }
                EventInfo eventInfo = method.getAnnotation(EventInfo.class);
                if(eventInfo.type() != event.getClass()) {
                    continue;
                }
                try {
                    method.invoke(script, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.log(Level.WARNING, "Failed to invoke event method " + method.getName(), e);
                }
            }
        }
    }

    @Override
    public Script getScript(String uuid) {
        return scripts.get(uuid);
    }

    @Override
    public List<Script> getScripts() {
        return List.copyOf(scripts.values());
    }
}
