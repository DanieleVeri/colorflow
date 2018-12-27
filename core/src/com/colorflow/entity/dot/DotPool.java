package com.colorflow.entity.dot;

import com.badlogic.gdx.utils.Pool;
import com.colorflow.entity.Path;
import com.colorflow.utility.Position;

/**
 * Created by daniele on 03/05/17.
 */

public class DotPool extends Pool<Dot> {

    private static DotPool instance = new DotPool();

    private DotPool() {
    }

    public static DotPool getInstance() {
        return instance;
    }

    @Override
    protected Dot newObject() {
        return new Dot();
    }

    public Dot get(Dot.Type type, Color color, Path.Type pathType, Position.Radial start, float speed) {
        Dot d = obtain();
        d.set(type, color, pathType, start, speed);
        return d;
    }
}
