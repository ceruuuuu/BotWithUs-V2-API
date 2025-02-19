package net.botwithus.rs3.login;

public enum LoginResponse {
    SUCCESS(2),
    INCORRECT(3),
    ALREADY_LOGGED_IN(5),
    MEMBER_WORLD(12),
    TOO_MANY_ATTEMPTS(16),
    LOCKED(18),
    INVALID_SESSION(48),
    UNKNOWN(-1);

    private final int id;

    LoginResponse(int id) {
        this.id = id;
    }

    public static LoginResponse fromId(int id) {
        for (LoginResponse response : values()) {
            if (response.id == id) {
                return response;
            }
        }
        return UNKNOWN;
    }
}
