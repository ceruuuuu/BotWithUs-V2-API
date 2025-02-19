package net.botwithus.rs3.entities.types;

public enum HitmarkType {
    MELEE(133),
    MELEE_CRITICAL(134),
    RANGED(136),
    RANGED_CRITICAL(137),
    MAGIC(139),
    MAGIC_CRITICAL(140),
    NECROMANCY(477),
    NECROMANCY_CRITICAL(478),
    DODGE(141),
    POISON(142),
    HEAL(143),
    TYPELESS(144),
    MISS(482);

    private final int id;

    HitmarkType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static HitmarkType fromId(int id) {
        for (HitmarkType type : HitmarkType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
