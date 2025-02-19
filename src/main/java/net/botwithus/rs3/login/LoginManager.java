package net.botwithus.rs3.login;

import net.botwithus.rs3.login.internal.MutableLoginManager;

import java.util.ArrayList;
import java.util.Collection;

import static net.botwithus.rs3.login.internal.MutableLoginManager.LOGIN_PROGRESS;
import static net.botwithus.rs3.login.internal.MutableLoginManager.LOGIN_STATUS;

public sealed abstract class LoginManager permits MutableLoginManager {

    public static Collection<World> getGameWorlds() {
        return new ArrayList<>(MutableLoginManager.GAME_WORLDS.values());
    }

    public static int getLoginResponseId() {
        return LOGIN_STATUS;
    }

    public static int getLoginProgress() {
        return LOGIN_PROGRESS;
    }

    public static LoginResponse getLoginResponse() {
        return LoginResponse.fromId(LOGIN_STATUS);
    }
}
