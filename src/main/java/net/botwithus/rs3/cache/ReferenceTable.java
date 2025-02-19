package net.botwithus.rs3.cache;

import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ReferenceTable {

    private static final Logger log = Logger.getLogger(ReferenceTable.class.getName());

    public final SortedMap<Integer, Archive> archives = new TreeMap<>();
    private final Filesystem filesystem;
    private final int index;
    private final int format = 7;
    private int version = 0;
    private int mask = 0;

    public ReferenceTable(Filesystem filesystem, int index) {
        this.filesystem = filesystem;
        this.index = index;
    }

    public void decode(ByteBuffer buffer) {
        int format = buffer.get();
        if (format < 5 || format > 7) {
            log.severe("Reference table format " + format + " is not supported.");
        }
        version = format >= 6 ? buffer.getInt() : 0;
        mask = buffer.get();

        boolean hasNames = (mask & 0x1) != 0;
        boolean hasWhirlpools = (mask & 0x2) != 0;
        boolean hasSizes = (mask & 0x4) != 0;
        boolean hasHashes = (mask & 0x8) != 0;

        Supplier<Integer> readFormatInt;
        if (format >= 7) {
            readFormatInt = () -> getSmartInt(buffer);
        } else {
            readFormatInt = () -> buffer.getShort() & 0xFFFF;
        }
        int[] archiveIds = new int[readFormatInt.get()];
        for (int i = 0; i < archiveIds.length; i++) {
            int archiveId = readFormatInt.get() + (i == 0 ? 0 : archiveIds[i - 1]);
            archiveIds[i] = archiveId;
            archives.put(archiveId, new Archive(archiveId));
        }
        if (hasNames) {
            for (int archiveId : archiveIds) {
                archives.get(archiveId).name = buffer.getInt();
            }
        }
        for (int archiveId : archiveIds) {
            archives.get(archiveId).crc = buffer.getInt();
        }
        if (hasHashes) {
            for (int archiveId : archiveIds) {
                archives.get(archiveId).hash = buffer.getInt();
            }
        }
        if (hasWhirlpools) {
            for (int archiveId : archiveIds) {
                byte[] whirlpool = new byte[64];
                buffer.get(whirlpool);
                archives.get(archiveId).whirlpool = whirlpool;
            }
        }
        if (hasSizes) {
            for (int archiveId : archiveIds) {
                Archive archive = archives.get(archiveId);
                archive.compressedSize = buffer.getInt();
                archive.uncompressedSize = buffer.getInt();
            }
        }
        for (int archiveId : archiveIds) {
            archives.get(archiveId).version = buffer.getInt();
        }
        int[][] archiveFileIds = new int[archives.size()][];
        for (int i = 0; i < archiveFileIds.length; i++) {
            archiveFileIds[i] = new int[readFormatInt.get()];
        }
        for (int i = 0; i < archiveIds.length; i++) {
            Archive archive = archives.get(archiveIds[i]);
            int[] fileIds = archiveFileIds[i];
            int fileId = 0;
            for (int j = 0; j < fileIds.length; j++) {
                fileId += readFormatInt.get();
                archive.files.put(fileId, new ArchiveFile(fileId));
                fileIds[j] = fileId;
            }
            archiveFileIds[i] = fileIds;
        }
        if (hasNames) {
            for (int i = 0; i < archiveIds.length; i++) {
                Archive archive = archives.get(archiveIds[i]);
                int[] fileIds = archiveFileIds[i];
                for (int fileId : fileIds) {
                    int name = buffer.getInt();
                    if (archive.files.containsKey(fileId)) {
                        archive.files.get(fileId).setName(name);
                    }
                }
            }
        }
    }

    public int getHighestEntry() {
        return archives.isEmpty() ? 0 : archives.lastKey() + 1;
    }

    public int getArchiveSize(int index) {
        if ((mask & 0x4) != 0) {
            Archive archive = archives.get(index);
            if (archive != null) {
                return archive.compressedSize;
            }
        } else {
            // Implementation not shown
        }
        return 0;
    }

    public long getTotalCompressedSize() {
        long sum = 0L;
        for (Archive value : archives.values()) {
            sum += value.compressedSize;
        }
        return sum;
    }


    public Archive loadArchive(int id) throws Exception {
        Archive archive = archives.get(id);
        if (archive == null || archive.loaded) {
            return archive;
        }

        ByteBuffer raw = filesystem.read(index, id);
        if (raw == null) {
            log.warning("Failed to load archive " + id);
            return null;
        }
        byte[] file = Container.decode(raw).getData();
        archive.decodeSqlite(ByteBuffer.wrap(file));
        return archive;
    }

    private int getSmartInt(ByteBuffer buffer) {
        if (buffer.get(buffer.position()) < 0) {
            return buffer.getInt() & 0x7FFFFFFF;
        }
        return buffer.getShort() & 0xFFFF;
    }
}
