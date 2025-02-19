package net.botwithus.rs3.stats;

import java.util.function.Function;

public class Skills {

    public static int getCurrentLevel(Skill skill) {
        return resolve(skill, Stat::getCurrentLevel);
    }

    public static int getLevel(Skill skill) {
        return resolve(skill, Stat::getLevel);
    }

    public static int getMaxLevel(Skill skill) {
        return resolve(skill, Stat::getMaxLevel);
    }

    public static int getExperience(Skill skill) {
        return resolve(skill, Stat::getExperience);
    }

    private static int resolve(Skill skill, Function<Stat, Integer> function) {
        Integer result = function.apply(skill.getStat());
        return result == null ? -1 : result;
    }
}
