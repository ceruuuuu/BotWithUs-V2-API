package net.botwithus.events.types;

import net.botwithus.events.Event;
import net.botwithus.rs3.stats.Skill;

public class SkillEvent implements Event {

    private final Skill skill;
    private final int gainedExperience;

    public SkillEvent(Skill skill, int gainedExperience) {
        this.skill = skill;
        this.gainedExperience = gainedExperience;
    }

    public Skill getStats() {
        return skill;
    }

    public int getGainedExperience() {
        return gainedExperience;
    }
}
