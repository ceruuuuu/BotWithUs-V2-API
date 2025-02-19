package net.botwithus.rs3.cache.assets.vars;

public enum VarDomainType {
    PLAYER(0),
    NPC(1),
    CLIENT(2),
    //WORLD(3),
    //REGION(4),
    OBJECT(5),
    CLAN(6),
    CLAN_SETTING(7),
    PLAYER_GROUP(8);

    private static final VarDomainType[] domains = values();
    private final int id;

    VarDomainType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static VarDomainType fromId(int id) {
        for (VarDomainType domain : domains) {
            if (domain.getId() == id) {
                return domain;
            }
        }
        return VarDomainType.PLAYER;
    }
}