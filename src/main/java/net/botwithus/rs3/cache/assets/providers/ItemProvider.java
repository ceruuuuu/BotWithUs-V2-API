package net.botwithus.rs3.cache.assets.providers;

import net.botwithus.rs3.cache.Archive;
import net.botwithus.rs3.cache.ArchiveFile;
import net.botwithus.rs3.cache.Filesystem;
import net.botwithus.rs3.cache.ReferenceTable;
import net.botwithus.rs3.cache.assets.ConfigProvider;
import net.botwithus.rs3.cache.assets.items.ItemDefinition;
import net.botwithus.rs3.cache.assets.items.ItemLoader;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItemProvider implements ConfigProvider<ItemDefinition> {
    private static final Logger log = Logger.getLogger(ItemProvider.class.getName());

    private final ItemLoader loader;
    private final Filesystem fs;

    private final Map<Integer, ItemDefinition> items;

    private final ParamProvider paramProvider;

    public ItemProvider(Filesystem fs, ParamProvider paramProvider) {
        this.loader = new ItemLoader();
        this.fs = fs;
        this.items = new HashMap<>();
        this.paramProvider = paramProvider;
    }

    @Override
    public String name() {
        return "item_types";
    }

    @Override
    public ItemDefinition provide(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        try {
            ReferenceTable table = fs.getReferenceTable(19, false);
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
            ItemDefinition item = new ItemDefinition(id);
            loader.load(item, ByteBuffer.wrap(file.getData()));

            if(item.getNotedTemplate() != -1) {
                item.toNote(provide(item.getNotedItemId()));
            }

            items.put(id, item);
            return item;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load reference table 19", e);
            return null;
        }
    }
}
