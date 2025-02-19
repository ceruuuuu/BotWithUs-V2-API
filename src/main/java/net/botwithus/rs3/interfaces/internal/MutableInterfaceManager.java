package net.botwithus.rs3.interfaces.internal;

import net.botwithus.rs3.interfaces.InterfaceManager;

import java.util.HashMap;
import java.util.Map;

public final class MutableInterfaceManager extends InterfaceManager {
    public static Map<Integer, MutableInterface> INTERFACES = new HashMap<>();

    public static MutableComponent TARGET;

}
