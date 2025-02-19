package net.botwithus.rs3.cache.assets.providers;

import net.botwithus.rs3.cache.Archive;
import net.botwithus.rs3.cache.ArchiveFile;
import net.botwithus.rs3.cache.Filesystem;
import net.botwithus.rs3.cache.ReferenceTable;
import net.botwithus.rs3.cache.assets.ConfigProvider;
import net.botwithus.rs3.cache.assets.so.SceneObjectDefinition;
import net.botwithus.rs3.cache.assets.so.SceneObjectLoader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SceneObjectProvider implements ConfigProvider<SceneObjectDefinition> {

    private static final Logger log = Logger.getLogger(SceneObjectProvider.class.getName());

    private final Filesystem fs;
    private final SceneObjectLoader loader;

    private final Map<Integer, SceneObjectDefinition> cache;

    public SceneObjectProvider(Filesystem fs) {
        this.fs = fs;
        this.loader = new SceneObjectLoader();
        this.cache = new HashMap<>();
    }

    @Override
    public String name() {
        return "loc_types";
    }

    @Override
    public SceneObjectDefinition provide(int id) {
        try {
            ReferenceTable table = fs.getReferenceTable(16, false);
            if (table == null) {
                return null;
            }
            int archiveId = id >> 8;
            int fileId = id & ((1 << 8) - 1);
            Archive archive = table.loadArchive(archiveId);
            if (archive == null) {
                return null;
            }
            ArchiveFile file = archive.files.get(fileId);
            if (file == null) {
                return null;
            }
            SceneObjectDefinition type = new SceneObjectDefinition(id);
            loader.load(type, ByteBuffer.wrap(file.getData()));
            cache.put(id, type);
            return type;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load loc type " + id, e);
            return null;
        }
    }
}
