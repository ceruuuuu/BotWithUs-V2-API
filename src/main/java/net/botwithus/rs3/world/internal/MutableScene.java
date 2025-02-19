package net.botwithus.rs3.world.internal;

import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.entities.internal.MutableItemStack;
import net.botwithus.rs3.entities.internal.MutableProjectile;
import net.botwithus.rs3.entities.internal.MutableSceneObject;
import net.botwithus.rs3.entities.internal.MutableSpotAnimation;
import net.botwithus.rs3.world.Scene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MutableScene extends Scene {

    public static final Map<Integer, PathingEntity> NPCS = new HashMap<>();

    public static final Map<Integer, PathingEntity> PLAYERS = new HashMap<>();

    public static final Map<Integer, MutableSceneObject> SCENE_OBJECTS = new HashMap<>();

    public static final Map<Integer, MutableSpotAnimation> SPOT_ANIMATIONS = new HashMap<>();

    public static final Map<Integer, MutableItemStack> ITEM_STACKS = new HashMap<>();

    public static final Map<Integer, MutableProjectile> PROJECTILES = new HashMap<>();


    public static final Set<Integer> UPDATING_NPCS = new HashSet<>();
    public static final Set<Integer> UPDATING_PLAYERS = new HashSet<>();
    public static final Set<Integer> UPDATING_SCENE_OBJECTS = new HashSet<>();
    public static final Set<Integer> UPDATING_SPOT_ANIMATIONS = new HashSet<>();
    public static final Set<Integer> UPDATING_ITEM_STACKS = new HashSet<>();

    public static final Set<Integer> UPDATING_PROJECTILES = new HashSet<>();
}
