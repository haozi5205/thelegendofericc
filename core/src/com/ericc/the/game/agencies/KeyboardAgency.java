package com.ericc.the.game.agencies;

import com.ericc.the.game.Direction;
import com.ericc.the.game.KeyboardController;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.VeryUglyGlobalState;
import com.ericc.the.game.actions.Action;
import com.ericc.the.game.actions.Actions;
import com.ericc.the.game.components.PositionComponent;
import com.ericc.the.game.components.SyncComponent;
import com.ericc.the.game.map.Map;
import com.ericc.the.game.utils.GridPoint;

public class KeyboardAgency implements Agency {
    private KeyboardController controller;

    public KeyboardAgency(KeyboardController controller) {
        this.controller = controller;
    }

    private boolean checkIfCanMove(Map map, GridPoint xy) {
        return map.isPassable(xy.x, xy.y) || !map.isFloor(xy.x, xy.y);
    }

    private boolean checkIfCanAttack(Map map, GridPoint xy) {
        return Mappers.hostile.has(map.getEntity(xy));
    }

    private Action handleDirectionalInput(PositionComponent pos, Direction direction) {
        GridPoint targetXY = pos.xy.add(GridPoint.fromDirection(direction));
        if (checkIfCanMove(pos.map, targetXY)) {
            if (VeryUglyGlobalState.playerRunning) {
                return Actions.RUN(direction);
            } else {
                return Actions.MOVE(direction);
            }
        } else if (checkIfCanAttack(pos.map, targetXY)) {
            return Actions.ATTACK(direction);
        } else {
            return SyncComponent.SYNC;
        }
    }

    @Override
    public Action chooseAction(PositionComponent pos) {
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
        } else {
            return SyncComponent.SYNC;
        }
    }
}
