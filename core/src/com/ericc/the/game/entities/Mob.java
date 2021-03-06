package com.ericc.the.game.entities;

import com.badlogic.ashley.core.Entity;
import com.ericc.the.game.Models;
import com.ericc.the.game.agencies.AstarAgency;
import com.ericc.the.game.components.*;
import com.ericc.the.game.drops.ExampleDrop;
import com.ericc.the.game.map.Map;
import com.ericc.the.game.utils.GridPoint;

public class Mob extends Entity {
    public Mob(GridPoint xy, Map map) {
        add(new PositionComponent(xy, map));
        add(new RenderableComponent(Models.slimeSmall));
        add(new StatsComponent(45, 30, 10, 120, false, 2));
        add(new AgencyComponent(new AstarAgency(), false));
        add(new CollisionComponent(CollisionComponent.Type.LIVING));
        add(new HostileTag());
        add(new ExperienceWorthComponent(300));
        add(new HealthBarComponent(Models.healthBar));
        add(new AnimationComponent());
        add(new DropComponent(new ExampleDrop()));
    }
}
