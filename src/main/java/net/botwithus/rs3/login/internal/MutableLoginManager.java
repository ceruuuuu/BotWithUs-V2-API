package net.botwithus.rs3.login.internal;

import net.botwithus.rs3.login.LoginManager;

import java.util.HashMap;
import java.util.Map;

public final class MutableLoginManager extends LoginManager {

    public static final Map<Integer, MutableWorld> GAME_WORLDS = new HashMap<>();

    public static int LOGIN_PROGRESS = 0;

    public static int LOGIN_STATUS = 0;

}
