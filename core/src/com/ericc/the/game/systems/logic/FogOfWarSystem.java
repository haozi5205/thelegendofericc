package com.ericc.the.game.systems.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.components.DirtyFlag;
import com.ericc.the.game.components.FieldOfViewComponent;
import com.ericc.the.game.components.PlayerTag;
import com.ericc.the.game.components.PositionComponent;
import com.ericc.the.game.utils.GridPoint;

public class FogOfWarSystem extends EntitySystem {

    private ImmutableArray<Entity> entities; ///< all entities able to discover new tiles

    public FogOfWarSystem(int priority) {
        super(priority); // Depends on FieldOfViewSystem
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                PositionComponent.class, PlayerTag.class,
                FieldOfViewComponent.class, DirtyFlag.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            FieldOfViewComponent fov = Mappers.fov.get(entity);
            PositionComponent pos = Mappers.position.get(entity);

            for (GridPoint point : fov.points) {
                pos.map.markAsSeenByPlayer(point);
            }
        }
    }
}
