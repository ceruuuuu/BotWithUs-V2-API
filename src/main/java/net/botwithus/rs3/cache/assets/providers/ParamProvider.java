package net.botwithus.rs3.cache.assets.providers;

import net.botwithus.rs3.cache.Archive;
import net.botwithus.rs3.cache.ArchiveFile;
import net.botwithus.rs3.cache.Filesystem;
import net.botwithus.rs3.cache.ReferenceTable;
import net.botwithus.rs3.cache.assets.ConfigProvider;
import net.botwithus.rs3.cache.assets.params.ParamDefinition;
import net.botwithus.rs3.cache.assets.params.ParamLoader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ParamProvider implements ConfigProvider<ParamDefinition> {

    private static final Logger log = Logger.getLogger(ItemProvider.class.getName());
    private final Filesystem fs;

    private final ParamLoader loader;

    private final Map<Integer, ParamDefinition> params;

    public ParamProvider(Filesystem fs) {
        this.fs = fs;
        this.params = new HashMap<>();
        this.loader = new ParamLoader();
    }

    @Override
    public String name() {
        return "param_types";
    }

    @Override
    public ParamDefinition provide(int id) {
        if (params.containsKey(id)) {
            return params.get(id);
        }
        try {
            ReferenceTable table = fs.getReferenceTable(2, false);
            if (table == null) {
                return null;
            }
            Archive archive = table.loadArchive(11);
            if (archive == null) {
                return null;
            }
            ArchiveFile file = archive.files.get(id);
            if (file == null) {
                return null;
            }
            ParamDefinition param = new ParamDefinition(id);
            loader.load(param, ByteBuffer.wrap(file.getData()));
            params.put(id, param);
            return param;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load reference table", e);
            return null;
        }
    }
}
