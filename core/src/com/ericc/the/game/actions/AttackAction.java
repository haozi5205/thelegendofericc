package com.ericc.the.game.actions;

import com.ericc.the.game.Direction;

public class AttackAction extends Action {
    public Direction direction;

    public AttackAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public int getDelay() {
        return 100;
    }
}
