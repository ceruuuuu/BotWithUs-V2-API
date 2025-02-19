package net.botwithus.rs3.cache;

import net.botwithus.rs3.cache.assets.providers.VarProvider;
import net.botwithus.rs3.cache.assets.vars.VarType;
import net.botwithus.rs3.cache.sqlite.SqliteFilesystem;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class CacheReadTest {

    @Test
    public void readItemType() {
        try (SqliteFilesystem fs = new SqliteFilesystem(Path.of("C:\\Program Files (x86)\\Steam\\steamapps\\common\\RuneScape\\RuneScape"))) {
            VarProvider provider = new VarProvider(fs);

            for (int i = 0; i < provider.capacity(); i++) {
                VarType type = provider.provide(i);
                if (type != null) {
                    System.out.println(type.getVarId() + " " + type.getDomainType() + " " + type.getType());
                }
            }
        } catch (Exception e) {
        }
    }

}
