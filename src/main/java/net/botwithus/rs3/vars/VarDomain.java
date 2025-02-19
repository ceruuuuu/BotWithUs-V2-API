package net.botwithus.rs3.vars;

import net.botwithus.rs3.cache.assets.ConfigManager;
import net.botwithus.rs3.cache.assets.providers.VarBitProvider;
import net.botwithus.rs3.cache.assets.vars.VarBitType;

import java.util.Optional;
import java.util.ServiceLoader;

public interface VarDomain {

    int getVarValue0(int id);

    static int getVarValue(int id) {
        Optional<VarDomain> first = ServiceLoader.load(VarDomain.class).findFirst();
        return first.map(varDomain -> varDomain.getVarValue0(id)).orElse(0);
    }

    static int getVarBitValue(int id) {
        VarBitProvider provider = ConfigManager.getVarBitProvider();
        VarBitType type = provider.provide(id);
        if (type == null) {
            return 0;
        }
        int value = getVarValue(type.getVarId());
        int mask = (1 << ((type.getMsb() - type.getLsb()) + 1)) - 1;
        return (value >> type.getLsb()) & mask;
    }
}
