package net.botwithus.modules.login;

import net.botwithus.imgui.ImGui;
import net.botwithus.modules.BotModule;
import net.botwithus.modules.BotModuleInfo;
import net.botwithus.rs3.client.Client;
import net.botwithus.rs3.cs2.ScriptDescriptor;
import net.botwithus.rs3.cs2.ScriptHandle;
import net.botwithus.rs3.login.LoginManager;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.util.Timer;

import java.util.logging.Logger;

@BotModuleInfo(name = "Auto Login", author = "BotWithUs", description = "Automatically logs in to the game", version = "1.0", states = {ClientState.LOGIN, ClientState.LOBBY})
public final class AutoLogin implements BotModule {

    private static final Logger log = Logger.getLogger(AutoLogin.class.getName());

    private static final AutoLogin INSTANCE = new AutoLogin();

    private final ScriptHandle LOGIN_SCRIPT = ScriptHandle.of(15259, ScriptDescriptor.ofVoid());
    private final ScriptHandle LOBBY_SCRIPT = ScriptHandle.of(15607, ScriptDescriptor.ofVoid());

    private boolean enabled = false;

    private boolean isVisible;

    private final Timer timer = new Timer();
    private long wait = 0;

    @Override
    public void run() {
        if(!enabled) {
            return;
        }
        if(LoginManager.getLoginProgress() > 0) {
            return;
        }
        if(!timer.elapsed(wait)) {
            return;
        }
        timer.reset();

        ClientState state = Client.getState();
        if(state == ClientState.LOGIN && LOGIN_SCRIPT != null) {
            LOGIN_SCRIPT.invokeExact();
            wait = 5000;
        } else if(state == ClientState.LOBBY && LOBBY_SCRIPT != null) {
            LOBBY_SCRIPT.invokeExact();
            wait = 5000;
        } else {
            log.warning("Invalid client state: " + state);
        }
    }

    @Override
    public void onDraw(Workspace workspace) {
        if(ImGui.begin("Auto Login",0)) {
            ImGui.text("Progress: " + LoginManager.getLoginProgress());
            ImGui.text("Login Status: " + LoginManager.getLoginStatus());
            ImGui.separator();
            enabled = ImGui.checkbox("Enabled", enabled);
            ImGui.end();
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    public static BotModule provider() {
        return INSTANCE;
    }
}
