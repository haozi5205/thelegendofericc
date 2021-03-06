package com.ericc.the.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.ericc.the.game.components.*;


/**
 * ComponentMappers are the preferred (fastest) way of retrieving components from entities.
 * Instead of using Entity.getComponent() or creating a redundant ComponentMapper every time,
 * use the statics from here.
 */
public class Mappers {
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<RenderableComponent> renderable = ComponentMapper.getFor(RenderableComponent.class);
    public static final ComponentMapper<FieldOfViewComponent> fov = ComponentMapper.getFor(FieldOfViewComponent.class);
    public static final ComponentMapper<StatsComponent> stats = ComponentMapper.getFor(StatsComponent.class);
    public static final ComponentMapper<CameraComponent> camera = ComponentMapper.getFor(CameraComponent.class);
    public static final ComponentMapper<AgencyComponent> agency = ComponentMapper.getFor(AgencyComponent.class);
    public static final ComponentMapper<CollisionComponent> collision = ComponentMapper.getFor(CollisionComponent.class);
    public static final ComponentMapper<PlayerTag> player = ComponentMapper.getFor(PlayerTag.class);
    public static final ComponentMapper<BossTag> boss = ComponentMapper.getFor(BossTag.class);
    public static final ComponentMapper<HostileTag> hostile = ComponentMapper.getFor(HostileTag.class);
    public static final ComponentMapper<DamageComponent> damage = ComponentMapper.getFor(DamageComponent.class);
    public static final ComponentMapper<HealthBarComponent> healthbar = ComponentMapper.getFor(HealthBarComponent.class);
    public static final ComponentMapper<ExperienceBarComponent> experienceBar = ComponentMapper.getFor(ExperienceBarComponent.class);
    public static final ComponentMapper<OwnedByComponent> owner = ComponentMapper.getFor(OwnedByComponent.class);
    public static final ComponentMapper<ExperienceWorthComponent> experienceWorth = ComponentMapper.getFor(ExperienceWorthComponent.class);
    public static final ComponentMapper<SafetyMapComponent> safety = ComponentMapper.getFor(SafetyMapComponent.class);
    public static final ComponentMapper<LootComponent> loot = ComponentMapper.getFor(LootComponent.class);
    public static final ComponentMapper<DropComponent> drop = ComponentMapper.getFor(DropComponent.class);
    public static final ComponentMapper<InventoryComponent> inventory = ComponentMapper.getFor(InventoryComponent.class);
    public static final ComponentMapper<DeadTag> dead = ComponentMapper.getFor(DeadTag.class);
}
