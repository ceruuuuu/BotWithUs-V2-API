package net.botwithus.scripts;

import net.botwithus.events.Event;

import java.net.URL;
import java.util.List;

public interface ScriptRepository {

    boolean load(URL url);

    boolean reload(Script script);

    Script getScript(String uuid);

    List<Script> getScripts();

    default ClassLoader newClassLoader(ClassLoader parent) {
        return new ScriptClassLoader(parent);
    }

    default boolean isRemote() {
        return false;
    }

    default void receiveEvent(Event event) {

    }

}
