package net.botwithus.ui.debug;

import net.botwithus.imgui.ImFlags;
import net.botwithus.imgui.ImGui;
import net.botwithus.rs3.login.World;
import net.botwithus.rs3.login.LoginManager;
import net.botwithus.rs3.login.Worlds;
import net.botwithus.ui.workspace.ExtInfo;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.ui.workspace.WorkspaceExtension;

import java.util.Comparator;
import java.util.Properties;

@ExtInfo(name = "Game Worlds")
public final class GameWorldDebug implements WorkspaceExtension {

    private boolean isGameWorldDebugVisible = false;

    private boolean isMemberFilter = false;

    @Override
    public void drawExtension(Workspace workspace) {
        if (isGameWorldDebugVisible) {
            if (ImGui.begin("Game Worlds", 0)) {
                isMemberFilter = ImGui.checkbox("Members Only", isMemberFilter);
                ImGui.separator();

                if (ImGui.beginTable("game_worlds_table", 5, ImFlags.ImGuiTable_Nice_Borders, 0, 0, 0)) {
                    ImGui.tableSetupColumn("World ID", 0, 0, 0);
                    ImGui.tableSetupColumn("World Population", 0, 0, 0);
                    ImGui.tableSetupColumn("World Activity", 0, 0, 0);
                    ImGui.tableSetupColumn("Members", 0, 0, 0);
                    ImGui.tableSetupColumn("Actions", 0, 0, 0);

                    ImGui.tableHeadersRow();

                    for (World world : Worlds.getLoaded().stream().sorted(Comparator.comparingInt(World::getWorldId)).toList()) {

                        if(isMemberFilter && !world.isMembers()) {
                            continue;
                        }

                        ImGui.tableNextRow(0, 0);
                        ImGui.tableNextColumn();
                        ImGui.text(Integer.toString(world.getWorldId()));
                        ImGui.tableNextColumn();
                        ImGui.text(Integer.toString(world.getPopulation()));
                        ImGui.tableNextColumn();
                        ImGui.text(world.getActivity());
                        ImGui.tableNextColumn();
                        ImGui.text(world.isMembers() ? "Yes" : "No");
                        ImGui.tableNextColumn();
                        if (ImGui.button("Hop##" + world.getWorldId(), 0, 0)) {
                            LoginManager.switchWorld(world);
                        }
                    }

                    ImGui.endTable();
                }
            }
            ImGui.end();
        }
    }

    @Override
    public void drawMenu(Workspace workspace) {
        isGameWorldDebugVisible = ImGui.menuItem("Game Worlds", null, isGameWorldDebugVisible, true);
    }

    @Override
    public void onLoad(Workspace workspace, Properties properties) {
        WorkspaceExtension.super.onLoad(workspace, properties);
        isGameWorldDebugVisible = Boolean.parseBoolean(properties.getProperty("isGameWorldDebugVisible", "false"));
        isMemberFilter = Boolean.parseBoolean(properties.getProperty("isMemberFilter", "false"));
    }

    @Override
    public void onSave(Workspace workspace, Properties properties) {
        WorkspaceExtension.super.onSave(workspace, properties);
        properties.setProperty("isGameWorldDebugVisible", Boolean.toString(isGameWorldDebugVisible));
        properties.setProperty("isMemberFilter", Boolean.toString(isMemberFilter));
    }

    private void row(String label, Object value) {
        ImGui.tableNextRow(0, 0);
        ImGui.tableNextColumn();
        ImGui.text(label);
        ImGui.tableNextColumn();
        ImGui.text(value == null ? "null" : value.toString());
    }
}
