package net.botwithus.rs3.entities.types;

public enum HeadbarType {
    STAMINA(5),
    ADRENALINE(7),
    PROGRESS(13),
    RESIDUAL_SOUL(21);

    // Ids ranging from 21 to 30 can all be for residual soul stacks
    // (feels messy to add them all and you can get the stack count more reliabliy from varps anyway)

    private final int id;

    HeadbarType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static HeadbarType fromId(int id) {
        for (HeadbarType type : HeadbarType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
