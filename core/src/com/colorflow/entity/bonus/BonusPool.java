package com.colorflow.entity.bonus;

import com.badlogic.gdx.utils.Pool;
import com.colorflow.entity.Path;
import com.colorflow.utility.Position;

/**
 * Created by daniele on 08/05/17.
 */

public class BonusPool extends Pool<Bonus> {

    private static BonusPool instance = new BonusPool();

    private BonusPool() {
    }

    public static BonusPool getInstance() {
        return instance;
    }

    @Override
    protected Bonus newObject() {
        return new Bonus();
    }

    public Bonus get(Bonus.Type type, Path.Type pathType, Position.Radial start, float speed) {
        Bonus b = obtain();
        b.set(type, pathType, start, speed);
        return b;
    }
}
