package com.ericc.the.game.actions;

import com.ericc.the.game.Direction;
import com.ericc.the.game.components.Model;
import com.ericc.the.game.utils.GridPoint;

public class Actions {
    public static WaitAction WAIT = new WaitAction(100);
    public static WaitAction LONG_WAIT = new WaitAction(500);
    public static WaitAction NOTHING = new WaitAction(1);

    public static MovementAction MOVE(Direction direction, int delay) {
        return new MovementAction(direction, delay, MovementAction.MovementType.WALK);
    }

    public static MovementAction RUN(Direction direction, int delay) {
        return new MovementAction(direction, delay, MovementAction.MovementType.RUN);
    }

    public static AOEAttack AOE_ATTACK(GridPoint relativePosition, Model model, Direction direction,
                                       int width, int height, int delay, int power) {
        return new AOEAttack(relativePosition, model, direction, width, height, delay, power);
    }

    public static AOEAttack DIRECTED_AOE_ATTACK(Model model, Direction direction,
                                                int width, int height, int delay, int power) {
        return new AOEAttack(model, direction, width, height, delay, power);
    }

    public static PushAction PUSH(Direction direction) {
        return new PushAction(direction, 150);
    }

    public static FlyAction FLY(int delay) {
        return new FlyAction(delay);
    }

    public static ShootAction SHOOT(Direction direction, int power) {
        return new ShootAction(direction, power);
    }
}
