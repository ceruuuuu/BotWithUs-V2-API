package net.botwithus.rs3.entities;

import net.botwithus.rs3.entities.types.EntityType;
import net.botwithus.rs3.entities.types.HeadbarType;
import net.botwithus.rs3.entities.types.HitmarkType;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.Interactable;
import net.botwithus.rs3.minimenu.MiniMenu;

import java.util.*;
import java.util.logging.Logger;

public abstract class PathingEntity extends Entity implements Interactable {

    private static final Logger log = Logger.getLogger(PathingEntity.class.getName());

    protected int index;

    protected int typeId;

    protected String name;
    protected String overheadText;
    protected boolean isMoving;

    protected int animationId;
    protected int stanceId;

    protected int health;
    protected int maxHealth;

    protected int followingIndex;

    protected EntityType followingType;

    protected Map<Integer, Headbar> headbars;
    protected Map<Integer, Hitmark> hitmarks;

    protected List<SpotAnimation> spotAnimations;

    protected PathingEntity(EntityType type) {
        super(type);
        this.headbars = new HashMap<>();
        this.hitmarks = new HashMap<>();
        this.spotAnimations = new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getOverheadText() {
        return overheadText;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Headbar getHeadbar(int id) {
        return headbars.get(id);
    }

    public Headbar getHeadbar(HeadbarType type) {
        return getHeadbar(type.getId());
    }

    public Collection<Headbar> getHeadbars() {
        return headbars.values();
    }

    public Hitmark getHitmark(int id) {
        return hitmarks.get(id);
    }

    public Hitmark getHitmark(HitmarkType type) {
        return getHitmark(type.getId());
    }

    public Collection<Hitmark> getHitmarks() {
        return hitmarks.values();
    }

    public int getAnimationId() {
        return animationId;
    }

    public int getStanceId() {
        return stanceId;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getFollowingIndex() {
        return followingIndex;
    }

    public EntityType getFollowingType() {
        return followingType;
    }

    public int getTypeId() {
        return typeId;
    }

    public List<SpotAnimation> getSpotAnimations() {
        return Collections.unmodifiableList(spotAnimations);
    }

    @Override
    public List<String> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public final boolean interact() {
        if (type == EntityType.PLAYER_ENTITY) {
            return MiniMenu.doAction(Action.PLAYER1, getIndex(), 0, 0);
        } else {
            return MiniMenu.doAction(Action.NPC1, getIndex(), 0, 0);
        }
    }

    @Override
    public final boolean interact(int option) {
        Action action = null;
        if (type == EntityType.PLAYER_ENTITY) {
            action = switch (option) {
                case 2 -> Action.PLAYER2;
                case 3 -> Action.PLAYER3;
                case 4 -> Action.PLAYER4;
                case 5 -> Action.PLAYER5;
                case 6 -> Action.PLAYER6;
                case 7 -> Action.PLAYER7;
                case 8 -> Action.PLAYER8;
                default -> Action.PLAYER1;
            };
        } else if (type == EntityType.NPC_ENTITY) {
            action = switch (option) {
                case 2 -> Action.NPC2;
                case 3 -> Action.NPC3;
                case 4 -> Action.NPC4;
                case 5 -> Action.NPC5;
                case 6 -> Action.NPC6;
                default -> Action.NPC1;
            };
        }
        if (action == null) {
            log.warning("Invalid option: " + option + " on " + type + " entity. Ignoring.");
            return false;
        }
        return MiniMenu.doAction(action, getIndex(), 0, 0);
    }
}