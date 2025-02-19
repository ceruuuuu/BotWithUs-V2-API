package net.botwithus.rs3.interfaces;

public record InterfaceAddress(int root, int component, int subComponent) {

    public static final InterfaceAddress INVALID = new InterfaceAddress(-1, -1);

    public InterfaceAddress(int interfaceId, int component) {
        this(interfaceId, component, -1);
    }

    @Override
    public String toString() {
        return "InterfaceAddress[root=" + root + ", component=" + component + ", subcomponent=" + subComponent + "]";
    }

    @Override
    public int hashCode() {
        return root << 16 | component;
    }
}
