package net.botwithus.ui;

import net.botwithus.imgui.ImGui;
import net.botwithus.modules.BotModule;
import net.botwithus.modules.BotModuleInfo;
import net.botwithus.scripts.Info;
import net.botwithus.scripts.RepositoryRegistry;
import net.botwithus.scripts.Script;
import net.botwithus.scripts.ScriptRepository;
import net.botwithus.scripts.repositories.LocalRepository;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.ui.workspace.WorkspaceExtension;

import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public final class BotWindow {

    private static final Logger log = Logger.getLogger(BotWindow.class.getName());

    private static final int ImGuiTableFlags_RowBg = 1 << 6;   // Set each RowBg color with ImGuiCol_TableRowBg or ImGuiCol_TableRowBgAlt (equivalent of calling TableSetBgColor with ImGuiTableBgFlags_RowBg0 on each row manually)
    private static final int ImGuiTableFlags_BordersInnerH = 1 << 7;   // Draw horizontal borders between rows.
    private static final int ImGuiTableFlags_BordersOuterH = 1 << 8;  // Draw horizontal borders at the top and bottom.
    private static final int ImGuiTableFlags_BordersInnerV = 1 << 9;   // Draw vertical borders between columns.
    private static final int ImGuiTableFlags_BordersOuterV = 1 << 10;  // Draw vertical borders on the left and right sides.

    private static final int ImGuiTableFlags_BordersH = ImGuiTableFlags_BordersInnerH | ImGuiTableFlags_BordersOuterH; // Draw horizontal borders.
    private static final int ImGuiTableFlags_BordersV = ImGuiTableFlags_BordersInnerV | ImGuiTableFlags_BordersOuterV; // Draw vertical borders.
    private static final int ImGuiTableFlags_BordersInner = ImGuiTableFlags_BordersInnerV | ImGuiTableFlags_BordersInnerH; // Draw inner borders.
    private static final int ImGuiTableFlags_BordersOuter = ImGuiTableFlags_BordersOuterV | ImGuiTableFlags_BordersOuterH; // Draw outer borders.
    private static final int ImGuiTableFlags_Borders = ImGuiTableFlags_BordersInner | ImGuiTableFlags_BordersOuter;   // Draw all borders.

    private static final int ImGuiTable_Nice = ImGuiTableFlags_RowBg | ImGuiTableFlags_Borders | ImGuiTableFlags_BordersOuter | ImGuiTableFlags_BordersInner | ImGuiTableFlags_BordersH | ImGuiTableFlags_BordersV;

    public static void draw() {
        try {
            Workspace current = WorkspaceManager.getManager().getCurrent();

            if (ImGui.beginMainMenuBar()) {
                if (ImGui.beginMenu("BotWithUs", true)) {
                    current.setScriptWindowOpen(ImGui.menuItem("Show Scripts", null, current.isScriptWindowOpen(), true));
                    current.setSettingsOpen(ImGui.menuItem("Show Settings", null, current.isSettingsOpen(), true));
                    current.setConsoleOpen(ImGui.menuItem("Show Console", null, current.isConsoleOpen(), true));
                    ImGui.separator();
                    if (ImGui.beginMenu("Modules", true)) {
                        for (BotModule module : ServiceLoader.load(BotModule.class)) {
                            BotModuleInfo info = module.getClass().getAnnotation(BotModuleInfo.class);
                            module.setVisible(ImGui.menuItem(info.name(), null, module.isVisible(), true));
                        }
                        ImGui.endMenu();
                    }
                    if (ImGui.beginMenu("Extensions", !current.getExtensions().isEmpty())) {
                        for (WorkspaceExtension ext : current.getExtensions()) {
                            ext.drawMenu(current);
                        }
                        ImGui.endMenu();
                    }
                    ImGui.endMenu();
                }
                if (ImGui.button("+", 0, 0)) {
                    WorkspaceManager.getManager().newWorkspace();
                }
                ImGui.separator();
                if (ImGui.beginTabBar("workspaces", 0)) {
                    for (Workspace workspace : WorkspaceManager.getManager()) {
                        if (ImGui.beginTabItem(workspace.getName() + "##" + workspace.getUuid(), 0)) {
                            if (current != workspace) {
                                WorkspaceManager.getManager().switchWorkspace(workspace);
                            }
                            ImGui.pushGroupId(workspace.getUuid());
                            drawWorkspace(workspace);
                            ImGui.popGroupId();
                            ImGui.endTabItem();
                        }
                        if (workspace.isDirty()) {
                            WorkspaceManager.save(workspace);
                        }
                    }
                    ImGui.endTabBar();
                }
                ImGui.endMainMenuBar();
            }
        } catch (Exception e) {
            log.log(java.util.logging.Level.SEVERE, "Error drawing bot window", e);
        }
    }

    private static void drawWorkspace(Workspace workspace) {
        if (workspace.isScriptWindowOpen()) {
            if (ImGui.begin("Scripts", 0)) {
                if (ImGui.beginTabBar("scriptrepos", 0)) {
                    if (ImGui.beginTabItem("Local Scripts", 0)) {
                        drawScriptRepository(RepositoryRegistry.getLocalRepository());
                        ImGui.endTabItem();
                    } else {
                        ImGui.text("Failed to draw local scripts tab.");
                    }
                    for (ScriptRepository repo : RepositoryRegistry.getRepositories()) {
                        Info info = repo.getClass().getAnnotation(Info.class);
                        if(info == null) {
                            continue;
                        }
                        if (ImGui.beginTabItem(info.name(), 0)) {
                            drawScriptRepository(repo);
                            ImGui.endTabItem();
                        } else {
                            ImGui.text("Failed to draw " + info.name() + " tab.");
                        }
                    }
                    ImGui.endTabBar();
                } else {
                    ImGui.text("Failed to draw scripts tab bar.");
                }
            } else {
                ImGui.text("Failed to draw scripts window.");
            }
            ImGui.end();
        }

        workspace.drawSettings();

        for (WorkspaceExtension ext : workspace.getExtensions()) {
            ext.drawExtension(workspace);
        }

        for (BotModule module : ServiceLoader.load(BotModule.class)) {
            if (module.isVisible()) {
                try {
                    module.onDraw(workspace);
                } catch (Throwable e) {
                    log.severe("Error running module: " + module.getClass().getName());
                    log.throwing("BotWindow", "drawWorkspace", e);
                }
            }
        }

        RepositoryRegistry.getLocalRepository().getScripts().forEach(script -> {
            if (script.isWindowVisible()) {
                script.draw(workspace);
            }
        });
    }

    private static void drawScriptRepository(ScriptRepository repo) {
        List<Script> scripts = repo.getScripts();
        if (ImGui.beginTable("scripts_list", 3, ImGuiTable_Nice, 0f, 0f, 0f)) {
            ImGui.tableSetupColumn("Name", 0, 0f, 0);
            ImGui.tableSetupColumn("Actions", 0, 0f, 0);
            ImGui.tableSetupColumn("Author", 0, 0f, 0);
            ImGui.tableHeadersRow();

            for (Script script : scripts) {
                Info info = script.getClass().getAnnotation(Info.class);
                ImGui.pushGroupId(info.name());
                ImGui.tableNextRow(0, 0f);
                ImGui.tableNextColumn();
                ImGui.text(info.name());
                ImGui.tableNextColumn();
                script.setActive(ImGui.checkbox("Active", script.isActive()));
                ImGui.sameLine(0, 0);
                script.setWindowVisible(ImGui.checkbox("Settings", script.isWindowVisible()));
                ImGui.sameLine(0, 3);
                if (ImGui.button("Reload", 50, 20)) {
                    repo.reload(script);
                }
                ImGui.tableNextColumn();
                ImGui.text(info.author());
                ImGui.popGroupId();
            }

            ImGui.endTable();
        }
    }

    private static void drawLocalScripts() {
        LocalRepository local = RepositoryRegistry.getLocalRepository();
        List<Script> scripts = local.getScripts();
        if (ImGui.beginTable("local_scripts", 3, ImGuiTable_Nice, 0f, 0f, 0f)) {
            ImGui.tableSetupColumn("Name", 0, 0f, 0);
            ImGui.tableSetupColumn("Actions", 0, 0f, 0);
            ImGui.tableSetupColumn("Author", 0, 0f, 0);
            ImGui.tableHeadersRow();

            for (Script script : scripts) {
                Info info = script.getClass().getAnnotation(Info.class);
                ImGui.pushGroupId(info.name());
                ImGui.tableNextRow(0, 0f);
                ImGui.tableNextColumn();
                ImGui.text(info.name());
                ImGui.tableNextColumn();
                script.setActive(ImGui.checkbox("Active", script.isActive()));
                ImGui.sameLine(0, 0);
                script.setWindowVisible(ImGui.checkbox("Settings", script.isWindowVisible()));
                ImGui.sameLine(0, 3);
                if (ImGui.button("Reload", 50, 20)) {
                    ScriptRepository repo = script.getRepository().get();
                    repo.reload(script);
                }
                ImGui.tableNextColumn();
                ImGui.text(info.author());
                ImGui.popGroupId();
            }

            ImGui.endTable();
        }
    }
}
