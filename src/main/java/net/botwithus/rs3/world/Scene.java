package net.botwithus.rs3.world;

import net.botwithus.rs3.entities.*;
import net.botwithus.rs3.world.internal.MutableScene;

import java.util.ArrayList;
import java.util.Collection;

public sealed abstract class Scene permits MutableScene {

    public static PathingEntity getNpc(int index) {
        return MutableScene.NPCS.get(index);
    }

    public static PathingEntity getPlayer(int index) {
        return MutableScene.PLAYERS.get(index);
    }

    public static Collection<PathingEntity> getPlayers() {
        return MutableScene.PLAYERS.values();
    }

    public static Collection<PathingEntity> getNpcs() {
        return MutableScene.NPCS.values();
    }

    public static SceneObject getSceneObject(int x, int y, int plane) {
        return MutableScene.SCENE_OBJECTS.get((plane << 28) | (x << 14) | y);
    }

    public static Collection<SceneObject> getSceneObjects() {
        return new ArrayList<>(MutableScene.SCENE_OBJECTS.values());
    }

    public static Collection<SpotAnimation> getSpotAnimations() {
        return new ArrayList<>(MutableScene.SPOT_ANIMATIONS.values());
    }

    public static Collection<ItemStack> getGroundItems() {
        return new ArrayList<>(MutableScene.ITEM_STACKS.values());
    }

    public static Collection<Projectile> getProjectiles() {
        return new ArrayList<>(MutableScene.PROJECTILES.values());
    }
}
