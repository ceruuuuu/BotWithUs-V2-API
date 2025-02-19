package net.botwithus.rs3.cache.assets.providers;

import net.botwithus.rs3.cache.Archive;
import net.botwithus.rs3.cache.ArchiveFile;
import net.botwithus.rs3.cache.Filesystem;
import net.botwithus.rs3.cache.ReferenceTable;
import net.botwithus.rs3.cache.assets.ConfigProvider;
import net.botwithus.rs3.cache.assets.npcs.NpcDefinition;
import net.botwithus.rs3.cache.assets.npcs.NpcLoader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NpcProvider implements ConfigProvider<NpcDefinition> {

    private static final Logger log = Logger.getLogger(NpcProvider.class.getName());

    private final Map<Integer, NpcDefinition> npcs;
    private final Filesystem fs;

    private final NpcLoader loader;

    public NpcProvider(Filesystem fs) {
        this.fs = fs;
        this.npcs = new HashMap<>();
        this.loader = new NpcLoader();
    }

    @Override
    public String name() {
        return "npc_types";
    }

    @Override
    public NpcDefinition provide(int id) {
        if (npcs.containsKey(id)) {
            return npcs.get(id);
        }
        try {
            ReferenceTable table = fs.getReferenceTable(18, false);
            if (table == null) {
                return null;
            }
            int archiveId = id >> 7;
            int fileId = id & ((1 << 7) - 1);
            Archive archive = table.loadArchive(archiveId);
            if (archive == null) {
                return null;
            }
            ArchiveFile file = archive.files.get(fileId);
            if (file == null) {
                return null;
            }
            NpcDefinition npc = new NpcDefinition(id);
            loader.load(npc, ByteBuffer.wrap(file.getData()));
            npcs.put(id, npc);
            return npc;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load reference table", e);
        }

        return null;
    }
}
