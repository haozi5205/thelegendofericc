package com.ericc.the.game.components;

import com.badlogic.ashley.core.Component;

public class StatsComponent implements Component {
    public static int FIRST_LEVEL_EXPERIENCE = 1000;
    public int intelligence;
    public int agility;
    public int strength;
    public int maxHealth;
    public int health;
    public int experience;
    public int level;
    public int maxExperience;
    public float delayMultiplier;
    public boolean canUseSkills;

    public StatsComponent(int intelligence, int agility, int strength, int health) {
        this(intelligence, agility, strength, health, true);
    }

    public StatsComponent(int intelligence, int agility, int strength, int health, boolean canUseSkills) {
        this.intelligence = intelligence;
        this.agility = agility;
        this.strength = strength;
        this.maxHealth = health;
        this.health = this.maxHealth;
        this.delayMultiplier = 1f;
        this.experience = 0;
        this.level = 1;
        this.maxExperience = FIRST_LEVEL_EXPERIENCE;
        this.canUseSkills = canUseSkills;
    }

    public StatsComponent(int intelligence, int agility, int strength, int health, float delayMultiplier) {
        this(intelligence, agility, strength, health);
        this.delayMultiplier = delayMultiplier;
    }
}
