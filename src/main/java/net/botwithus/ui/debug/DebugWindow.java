package net.botwithus.ui.debug;

import net.botwithus.imgui.ImFlags;
import net.botwithus.imgui.ImGui;
import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.entities.SceneObject;
import net.botwithus.rs3.world.Scene;
import net.botwithus.ui.workspace.ExtInfo;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.ui.workspace.WorkspaceExtension;

import java.util.Collection;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.logging.Logger;

@ExtInfo(name = "Debug Window")
public final class DebugWindow implements WorkspaceExtension {

    private static final Logger log = Logger.getLogger(DebugWindow.class.getName());

    private boolean isNpcWindowOpen = false;

    private boolean isSceneObjectOpen = false;

    private String npcName;

    private int npcTypeId;

    private double npcDistance;

    private String sceneObjectName;

    private int sceneObjectTypeId;

    private double sceneObjectDistance;

    private Predicate<SceneObject> filter = _ -> true;

    public DebugWindow() {
        this.npcName = "";
        this.npcTypeId = -1;
        this.npcDistance = 0.0d;
        this.sceneObjectName = "";
        this.sceneObjectTypeId = -1;
        this.sceneObjectDistance = 0.0d;
    }

    @Override
    public void drawExtension(Workspace workspace) {

        if (isNpcWindowOpen) {
            // Draw the NPC debug window
            drawNpcDebug(workspace);
        }

        if (isSceneObjectOpen) {
            // Draw the Scene Object debug window
            drawSceneObjectDebug(workspace);
        }

    }

    @Override
    public void drawMenu(Workspace workspace) {
        isNpcWindowOpen = ImGui.menuItem("NPC Debug", null, isNpcWindowOpen, true);
        isSceneObjectOpen = ImGui.menuItem("Scene Object Debug", null, isSceneObjectOpen, true);

    }

    public void drawSceneObjectDebug(Workspace workspace) {
        // Draw the Scene Object debug window
        if (ImGui.begin("Scene Object Debug", 0)) {
            Collection<SceneObject> sceneObjects = Scene.getSceneObjects();

            sceneObjectName = ImGui.inputText("Scene Object Name", sceneObjectName, 0);
            sceneObjectTypeId = ImGui.inputInt("Scene Object Type ID", sceneObjectTypeId, 1, 1, 0);
            sceneObjectDistance = ImGui.inputFloat("Distance", (float) sceneObjectDistance, 1, 1, 0);
            if(ImGui.button("Clear Search", 0, 0)) {
                filter = _ -> true;
                sceneObjectName = "";
                sceneObjectTypeId = -1;
                sceneObjectDistance = 0.0d;
            }
            if(!sceneObjectName.isEmpty() || sceneObjectTypeId != -1 || sceneObjectDistance > 0) {
                filter = _ -> false;
            }
            if(!sceneObjectName.isEmpty()) {
                filter = filter.or(sceneObject -> sceneObject.getName().toLowerCase().contains(sceneObjectName.toLowerCase()));
            }
            if(sceneObjectTypeId != -1) {
                filter = filter.or(sceneObject -> sceneObject.getTypeId() == sceneObjectTypeId);
            }
            if(sceneObjectDistance > 0) {
                filter = filter.or(sceneObject -> sceneObject.distance() <= sceneObjectDistance);
            }

            ImGui.separatorText("Scene Objects");
            if (sceneObjects.isEmpty()) {
                ImGui.text("No Scene Objects found.");
            } else {
                if (ImGui.beginTable("sceneObjects", 4, ImFlags.ImGuiTable_Nice_Borders, 0, 0, 0)) {
                    ImGui.tableSetupColumn("Name", 0, 0, 0);
                    ImGui.tableSetupColumn("ID", 0, 0, 0);
                    ImGui.tableSetupColumn("Hidden", 0, 0, 0);
                    ImGui.tableSetupColumn("Coordinate", 0, 0, 0);
                    ImGui.tableHeadersRow();
                    for (SceneObject sceneObject : sceneObjects.stream().filter(filter).toList()) {
                        ImGui.tableNextRow(0, 0);
                        ImGui.tableNextColumn();
                        ImGui.text(sceneObject.getName());
                        ImGui.tableNextColumn();
                        ImGui.text(String.valueOf(sceneObject.getTypeId()));
                        ImGui.tableNextColumn();
                        ImGui.text(String.valueOf(sceneObject.isHidden()));
                        ImGui.tableNextColumn();
                        ImGui.text(sceneObject.getCoordinate().x() + " " + sceneObject.getCoordinate().y() + " " + sceneObject.getCoordinate().z());
                    }
                    ImGui.endTable();
                }
            }
        }
        ImGui.end();
    }

    public void drawNpcDebug(Workspace workspace) {
        // Draw the NPC debug window
        if (ImGui.begin("NPC Debug", 0)) {
            Collection<PathingEntity> npcs = Scene.getNpcs();

            npcName = ImGui.inputText("NPC Name", npcName, 0);
            npcTypeId = ImGui.inputInt("NPC Type ID", npcTypeId, 1, 1, 0);
            npcDistance = ImGui.inputFloat("Distance", (float) npcDistance, 1, 1, 0);
            if (ImGui.button("Clear Search", 0, 0)) {
                npcName = "";
                npcTypeId = -1;
                npcDistance = 0.0d;
            }

            ImGui.separatorText("NPCS");
            //table
            if (npcs.isEmpty()) {
                ImGui.text("No NPCs found.");
            } else {
                if (ImGui.beginTable("npcs", 3, ImFlags.ImGuiTable_Nice_Borders, 0, 0, 0)) {
                    ImGui.tableSetupColumn("Name", 0, 0, 0);
                    ImGui.tableSetupColumn("ID", 0, 0, 0);
                    ImGui.tableSetupColumn("Coordinate", 0, 0, 0);
                    ImGui.tableHeadersRow();
                    for (PathingEntity npc : npcs) {

                        if (!npcName.isEmpty() && !npc.getName().toLowerCase().contains(npcName.toLowerCase())) {
                            continue;
                        }

                        if (npcTypeId != -1 && npc.getTypeId() != npcTypeId) {
                            continue;
                        }
                        if(npcDistance > 0) {
                            double v = npc.distance();
                            if(v > npcDistance) {
                                continue;
                            }
                        }

                        ImGui.tableNextRow(0, 0);
                        ImGui.tableNextColumn();
                        ImGui.text(npc.getName());
                        ImGui.tableNextColumn();
                        ImGui.text(String.valueOf(npc.getTypeId()));
                        ImGui.tableNextColumn();
                        ImGui.text(npc.getCoordinate().x() + " " + npc.getCoordinate().y() + " " + npc.getCoordinate().z());
                    }
                    ImGui.endTable();
                }
            }
        }
        ImGui.end();
    }

    @Override
    public void onLoad(Workspace workspace, Properties properties) {
        WorkspaceExtension.super.onLoad(workspace, properties);
        isNpcWindowOpen = Boolean.parseBoolean(properties.getProperty("isNpcWindowOpen"));
        npcName = properties.getProperty("npcName", "");
        npcTypeId = Integer.parseInt(properties.getProperty("npcTypeID", "-1"));
        npcDistance = Double.parseDouble(properties.getProperty("npcDistance", "0.0"));

        sceneObjectName = properties.getProperty("sceneObjectName", "");
        sceneObjectTypeId = Integer.parseInt(properties.getProperty("sceneObjectTypeID", "-1"));
        sceneObjectDistance = Double.parseDouble(properties.getProperty("sceneObjectDistance", "0.0"));
    }

    @Override
    public void onSave(Workspace workspace, Properties properties) {
        WorkspaceExtension.super.onSave(workspace, properties);
        properties.setProperty("isNpcWindowOpen", String.valueOf(isNpcWindowOpen));
        properties.setProperty("npcName", npcName);
        properties.setProperty("npcTypeID", String.valueOf(npcTypeId));
        properties.setProperty("npcDistance", String.valueOf(npcDistance));

        properties.setProperty("sceneObjectName", sceneObjectName);
        properties.setProperty("sceneObjectTypeID", String.valueOf(sceneObjectTypeId));
        properties.setProperty("sceneObjectDistance", String.valueOf(sceneObjectDistance));
    }

}
