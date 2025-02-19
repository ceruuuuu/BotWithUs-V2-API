package net.botwithus.rs3.cache.assets.providers;

import net.botwithus.rs3.cache.Archive;
import net.botwithus.rs3.cache.ArchiveFile;
import net.botwithus.rs3.cache.Filesystem;
import net.botwithus.rs3.cache.ReferenceTable;
import net.botwithus.rs3.cache.assets.ConfigProvider;
import net.botwithus.rs3.cache.assets.maps.RegionDefinition;
import net.botwithus.rs3.cache.assets.maps.SceneObjectSpawnLoader;
import net.botwithus.rs3.cache.assets.maps.TileLoader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MapProvider implements ConfigProvider<RegionDefinition> {

    private static final Logger log = Logger.getLogger(MapProvider.class.getName());

    private static final int LOC_FILE = 0;
    private static final int TILES_FILE = 3;

    private final Filesystem fs;
    private final Map<Integer, RegionDefinition> cache;

    private final SceneObjectSpawnLoader spawnLoader;
    private final TileLoader tileLoader;;

    public MapProvider(Filesystem fs) {
        this.fs = fs;
        this.cache = new HashMap<>();
        this.spawnLoader = new SceneObjectSpawnLoader();
        this.tileLoader = new TileLoader();
    }

    @Override
    public String name() {
        return "region_types";
    }

    @Override
    public RegionDefinition provide(int id) {
        if(cache.containsKey(id)) {
            return cache.get(id);
        }
        try {
            ReferenceTable table = fs.getReferenceTable(5, false);
            if (table == null) {
                return null;
            }
            int regionX = id >> 8 & 0xFF;
            int regionY = id & 0xFF;
            Archive archive = table.loadArchive(regionX | (regionY << 7));
            ArchiveFile locFile = archive.files.get(LOC_FILE);
            ArchiveFile tilesFile = archive.files.get(TILES_FILE);
            RegionDefinition type = new RegionDefinition(id);
            if (locFile != null) {
                spawnLoader.load(type, ByteBuffer.wrap(locFile.getData()));
            }
            if (tilesFile != null) {
                tileLoader.load(type, ByteBuffer.wrap(tilesFile.getData()));
            }
            cache.put(id, type);
            return type;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load region_types reference table", e);
        }
        return null;
    }
}
