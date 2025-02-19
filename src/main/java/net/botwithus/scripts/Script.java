package net.botwithus.scripts;

import net.botwithus.modules.BotModule;
import net.botwithus.modules.BotModuleInfo;
import net.botwithus.ui.WorkspaceManager;
import net.botwithus.ui.workspace.Workspace;

import java.net.URL;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Script implements Runnable {

    private static final Logger log = Logger.getLogger(Script.class.getName());

    private ScriptRepository repository;

    private URL url;

    private boolean isActive;

    private boolean isPaused;

    private boolean isWindowVisible;

    public void onInitialize() {

    }

    public void onActivation() {

    }

    public void onDeactivation() {

    }

    public void onDraw(Workspace workspace) {

    }

    public final void activate() {
        onActivation();
    }

    public final void deactivate() {
        onDeactivation();
    }

    public final void initialize() {
        onInitialize();
    }

    public final void draw(Workspace workspace) {
        onDraw(workspace);
    }

    public final void setRepository(ScriptRepository repository) {
        if (this.repository != null) {
            log.warning("Repository already set");
            return;
        }
        this.repository = repository;
    }

    public final void setUrl(URL url) {
        if (this.url != null) {
            log.warning("URL already set");
            return;
        }
        this.url = url;
    }

    public Optional<BotModule> getBotModule(String name) {
        ServiceLoader<BotModule> load = ServiceLoader.load(BotModule.class);
        for (ServiceLoader.Provider<BotModule> provider : load.stream().toList()) {
            Class<? extends BotModule> type = provider.type();
            if (type.isAnnotationPresent(BotModuleInfo.class)) {
                BotModuleInfo info = type.getAnnotation(BotModuleInfo.class);
                if (info.name().equals(name)) {
                    return Optional.of(provider.get());
                }
            }
        }
        return Optional.empty();
    }

    public void println(String message) {
        WorkspaceManager manager = WorkspaceManager.getManager();
        Workspace current = manager.getCurrent();
        current.getLogger().log(java.util.logging.Level.INFO, message);
    }

    public void println(String message, Object... args) {
        WorkspaceManager manager = WorkspaceManager.getManager();
        Workspace current = manager.getCurrent();
        current.getLogger().log(java.util.logging.Level.INFO, String.format(message, args));
    }

    public void println(Level level, String message) {
        WorkspaceManager manager = WorkspaceManager.getManager();
        Workspace current = manager.getCurrent();
        current.getLogger().log(level, message);
    }

    public void println(String message, Throwable t) {
        WorkspaceManager manager = WorkspaceManager.getManager();
        Workspace current = manager.getCurrent();
        current.getLogger().log(Level.SEVERE, message, t);
    }

    public void println(Throwable t) {
        WorkspaceManager manager = WorkspaceManager.getManager();
        Workspace current = manager.getCurrent();
        current.getLogger().log(Level.SEVERE, t.getMessage(), t);
    }

    public URL getUrl() {
        return url;
    }

    public final Optional<ScriptRepository> getRepository() {
        return Optional.ofNullable(repository);
    }

    public synchronized boolean isActive() {
        return isActive;
    }

    public synchronized void setActive(boolean active) {
        isActive = active;
    }

    public synchronized boolean isPaused() {
        return isPaused;
    }

    public synchronized void setPaused(boolean paused) {
        isPaused = paused;
    }

    public synchronized boolean isWindowVisible() {
        return isWindowVisible;
    }

    public synchronized void setWindowVisible(boolean windowVisible) {
        isWindowVisible = windowVisible;
    }
}
