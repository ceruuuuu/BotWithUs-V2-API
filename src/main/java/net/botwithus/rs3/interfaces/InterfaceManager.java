package net.botwithus.rs3.interfaces;

import net.botwithus.rs3.interfaces.internal.MutableInterfaceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public sealed abstract class InterfaceManager permits MutableInterfaceManager {

    public static boolean isOpen(int... interfaceIds) {
        if (interfaceIds.length == 0) {
            return false;
        }
        if(MutableInterfaceManager.INTERFACES.isEmpty()) {
            return false;
        }
        for (int id : interfaceIds) {
            Interface inter = getInterface(id);
            if (inter != null && inter.isOpen()) {
                return true;
            }
        }
        return false;
    }

    public static Interface getInterface(int interfaceId) {
        return MutableInterfaceManager.INTERFACES.get(interfaceId);
    }

    public static Collection<Interface> getInterfaces() {
        return new ArrayList<>(MutableInterfaceManager.INTERFACES.values().stream().sorted(Comparator.comparingInt(Interface::getInterfaceId)).toList());
    }

    public static Component getComponent(int root, int component, int... subComponents) {
        Interface parent = getInterface(root);
        if (parent == null) {
            return null;
        }

        Component result = parent.getComponent(component);
        if (result == null) {
            return null;
        }

        for (int sub : subComponents) {
            if (sub == -1) {
                continue;
            }
            Component subComponent = result.getSubComponent(sub);
            if (subComponent != null) {
                return subComponent;
            }
        }
        return result;
    }

    public static Component getComponent(int root, int component) {
        return getComponent(root, component, -1);
    }

    public static Component getComponent(InterfaceAddress address) {
        // Return null for an invalid address (-1 root or -1 component)
        return address.hashCode() > 0 ? getComponent(address.root(), address.component(), address.subComponent()) : null;
    }

    public static boolean hasTarget() {
        return MutableInterfaceManager.TARGET != null;
    }

    public static Component getTarget() {
        return MutableInterfaceManager.TARGET;
    }

}
