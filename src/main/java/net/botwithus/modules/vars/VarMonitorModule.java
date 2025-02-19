package net.botwithus.modules.vars;

import net.botwithus.events.types.VarpEvent;
import net.botwithus.imgui.ImFlags;
import net.botwithus.imgui.ImGui;
import net.botwithus.modules.BotModule;
import net.botwithus.modules.BotModuleInfo;
import net.botwithus.rs3.cache.assets.ConfigManager;
import net.botwithus.rs3.cache.assets.providers.VarProvider;
import net.botwithus.rs3.cache.assets.vars.VarBitType;
import net.botwithus.rs3.cache.assets.vars.VarDomainType;
import net.botwithus.rs3.cache.assets.vars.VarType;
import net.botwithus.rs3.vars.VarDomain;
import net.botwithus.rs3.world.ClientState;
import net.botwithus.scripts.RepositoryRegistry;
import net.botwithus.scripts.ScriptRepository;
import net.botwithus.ui.workspace.Workspace;

import java.util.HashMap;
import java.util.Map;

@BotModuleInfo(name = "Var Monitor", author = "DrJavatar", description = "Monitors RS3 vars", version = "1.0", states = {ClientState.LOBBY, ClientState.GAME})
public class VarMonitorModule implements BotModule {

    private static VarMonitorModule instance = new VarMonitorModule();

    private boolean enabled;

    private boolean isVisible;

    private final Map<Integer, Integer> varCache;
    private final Map<Integer, Integer> varbitCache;

    private final int varCount;
    private final int varbitCount;

    public VarMonitorModule() {
        enabled = false;
        isVisible = false;
        varCache = new HashMap<>();
        varbitCache = new HashMap<>();
        VarProvider provider = ConfigManager.getVarProvider();
        varCount = provider.capacity();
        varbitCount = ConfigManager.getVarBitProvider().capacity();
    }

    @Override
    public void onDraw(Workspace workspace) {
        if (!isVisible) {
            return;
        }
        if (ImGui.begin("Var Monitor", 0)) {
            if (ImGui.beginTabBar("vars_varbits", 0)) {
                if (ImGui.beginTabItem("Info", 0)) {
                    ImGui.text("Vars " + varCache.size());
                    ImGui.text("Varbits " + varbitCache.size());
                    ImGui.separator();
                    enabled = ImGui.checkbox("Enabled", enabled);
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Vars", 0)) {
                    drawVarTable();
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Varbits", 0)) {
                    drawVarbitTable();
                    ImGui.endTabItem();
                }
                ImGui.endTabBar();
            }
        }
        ImGui.end();
    }

    private void drawVarTable() {
        if (ImGui.beginTable("vars_table", 4, ImFlags.ImGuiTable_Nice_Borders, 0, 0, 0)) {
            ImGui.tableSetupColumn("Var ID", 0, 0, 0);
            ImGui.tableSetupColumn("Domain", 0, 0, 0);
            ImGui.tableSetupColumn("Type", 0, 0, 0);
            ImGui.tableSetupColumn("Value", 0, 0, 0);

            ImGui.tableHeadersRow();

            for (int i = 0; i < varCount; i++) {
                VarType type = ConfigManager.getVarProvider().provide(i);
                if(type == null) {
                    continue;
                }

                ImGui.tableNextRow(0, 0);
                ImGui.tableNextColumn();
                ImGui.text(String.valueOf(i));
                ImGui.tableNextColumn();
                ImGui.text(VarDomainType.fromId(type.getDomainType()).name());
                ImGui.tableNextColumn();
                ImGui.text(type.getType().name());
                ImGui.tableNextColumn();
                Integer value = varCache.get(i);
                ImGui.text(value == null ? "0" : String.valueOf(value));
            }

            ImGui.endTable();
        }
    }

    private void drawVarbitTable() {
        if (ImGui.beginTable("varbits_table", 4, ImFlags.ImGuiTable_Nice_Borders, 0, 0, 0)) {
            ImGui.tableSetupColumn("Varbit ID", 0, 0, 0);
            ImGui.tableSetupColumn("Domain", 0, 0, 0);
            ImGui.tableSetupColumn("Type", 0, 0, 0);
            ImGui.tableSetupColumn("Value", 0, 0, 0);

            ImGui.tableHeadersRow();

            for (int i = 0; i < varbitCount; i++) {
                VarBitType type = ConfigManager.getVarBitProvider().provide(i);
                if(type == null) {
                    continue;
                }
                if(type.getDomainType() != VarDomainType.PLAYER) {
                    continue;
                }

                try {
                    ImGui.tableNextRow(0, 0);
                    ImGui.tableNextColumn();
                    ImGui.text(String.valueOf(i));
                    ImGui.tableNextColumn();
                    ImGui.text(type.getDomainType().name());
                    ImGui.tableNextColumn();
                    VarType varType = ConfigManager.getVarProvider().provide(type.getVarId());
                    ImGui.text(varType.getType().name());
                    ImGui.tableNextColumn();
                    Integer value = varbitCache.get(i);
                    ImGui.text(value == null ? "0" : String.valueOf(value));
                } catch (Exception e) {
                    log.throwing("VarMonitorModule", "drawVarbitTable", e);
                }
            }
            ImGui.endTable();
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

    @Override
    public void run() {
        if (!enabled) {
            return;
        }
        for (int i = 0; i < varCount; i++) {
            int value = VarDomain.getVarValue(i);
            if(value == 0) {
                continue;
            }
            Integer oldValue = varCache.get(i);
            if (oldValue == null || value != oldValue) {
                sendEvent(i, oldValue == null ? 0 : oldValue, value, false);
                varCache.put(i, value);
            }
        }
        for (int i = 0; i < varbitCount; i++) {
            int value = VarDomain.getVarBitValue(i);
            if(value == 0) {
                continue;
            }
            Integer oldValue = varbitCache.get(i);
            if (oldValue == null || value != oldValue) {
                sendEvent(i, oldValue == null ? 0 : oldValue, value, true);
                varbitCache.put(i, value);
            }
        }
    }

    private void sendEvent(int varId, int oldValue, int value, boolean isVarbit) {
        VarpEvent event = new VarpEvent(varId, oldValue, value, isVarbit);
        if (RepositoryRegistry.hasOtherRepositories()) {
            for (ScriptRepository repository : RepositoryRegistry.getRepositories()) {
                repository.receiveEvent(event);
            }
        }
        RepositoryRegistry.getLocalRepository().receiveEvent(event);
    }

    public static VarMonitorModule provider() {
        return instance;
    }
}
