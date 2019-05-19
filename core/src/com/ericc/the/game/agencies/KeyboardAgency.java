package com.ericc.the.game.agencies;

import com.ericc.the.game.Direction;
import com.ericc.the.game.KeyboardController;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.Models;
import com.ericc.the.game.actions.Action;
import com.ericc.the.game.actions.Actions;
import com.ericc.the.game.components.CollisionComponent;
import com.ericc.the.game.components.PositionComponent;
import com.ericc.the.game.components.StatsComponent;
import com.ericc.the.game.map.Map;
import com.ericc.the.game.utils.GridPoint;

import java.util.Collections;

public class KeyboardAgency implements Agency {
    private KeyboardController controller;
    private boolean running;

    public KeyboardAgency(KeyboardController controller) {
        this.controller = controller;
    }

    private boolean checkIfCanMove(Map map, GridPoint xy) {
        return map.isPassable(xy.x, xy.y) || !map.isFloor(xy.x, xy.y);
    }

    private boolean checkIfCanAttack(Map map, GridPoint xy) {
        return Mappers.hostile.has(map.getEntity(xy));
    }

    private boolean checkIfCanPush(Map map, GridPoint xy) {
        CollisionComponent col = Mappers.collision.get(map.getEntity(xy));
        return col != null && col.type == CollisionComponent.Type.CRATE;
    }

    private Action handleDirectionalInput(PositionComponent pos, Direction direction) {
        GridPoint targetXY = pos.xy.add(GridPoint.fromDirection(direction));
        if (checkIfCanMove(pos.map, targetXY)) {
            if (running) {
                return Actions.RUN(direction, 50);
            } else {
                return Actions.MOVE(direction, 100);
            }
        } else if (checkIfCanPush(pos.map, targetXY)) {
            return Actions.PUSH(direction, 150);
        } else if (checkIfCanAttack(pos.map, targetXY)) {
            return Actions.AOE_ATTACK(Models.sword, Collections.singletonList(targetXY), direction, 100, 40);
        } else {
            return Actions.WAIT;
        }
    }

    @Override
    public Action chooseAction(PositionComponent pos, StatsComponent stats) {
        if (controller.right) {
            controller.right = false;
            return handleDirectionalInput(pos, Direction.RIGHT);
        } else if (controller.left) {
            controller.left = false;
            return handleDirectionalInput(pos, Direction.LEFT);
        } else if (controller.down) {
            controller.down = false;
            return handleDirectionalInput(pos, Direction.DOWN);
        } else if (controller.up) {
            controller.up = false;
            return handleDirectionalInput(pos, Direction.UP);
        } else if (controller.space) {
            controller.space = false;
            return Actions.LONG_WAIT;
        } else if (controller.q) {
            controller.q = false;
            return Actions.AOE_ATTACK(Models.explosion1, pos.map.calculateFOV(pos.xy, 100), pos.dir, 200, 30);
        } else if (controller.e) {
            controller.e = false;
            return Actions.AOE_ATTACK(Models.explosion3, pos.map.calculateRay(pos.xy, pos.dir, 5), pos.dir, 300, 40);
        } else if (controller.f) {
            controller.f = false;
            return Actions.SHOOT(pos.dir, 100, 40);
        } else if (controller.n) {
            controller.n = false;
            stats.delayMultiplier *= 2;
            return Actions.WAIT;
        } else if (controller.m) {
            controller.m = false;
            stats.delayMultiplier /= 2;
            return Actions.WAIT;
        } else if (controller.r) {
            controller.r = false;
            running = !running;
            return null;
        } else {
            return null;
        }
    }
}
