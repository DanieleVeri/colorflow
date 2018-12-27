package com.colorflow.entity;

import com.badlogic.gdx.utils.Pool;
import com.colorflow.utility.Position;

/**
 * Created by daniele on 09/05/17.
 */

public class Path implements Pool.Poolable {

    public enum Type {
        RADIAL, SPIRAL
    }

    private Type type;
    private Position.Radial pos;
    private float speed;

    public Path(Type type, Position.Radial pos, float speed) {
        this.pos = new Position.Radial(1, 1);
        set(type, pos, speed);
    }

    public void set(Type type, Position.Radial pos, float speed) {
        this.type = type;
        this.pos.setX(pos.getX());
        this.pos.setY(pos.getY());
        this.speed = speed;
    }

    public Position nextPos(float delta) {
        switch (type) {
            case RADIAL:
                pos.setDist(pos.getDistRadial() - speed);
                break;
            case SPIRAL:
                pos.setAngle(pos.getAngleRadial() - speed/2);
                pos.setDist(pos.getDistRadial() - speed);
                break;
            default:
                throw new IllegalStateException();
        }
        return pos;
    }

    public Position.Radial getPos() {
        return pos;
    }

    @Override
    public void reset() {

    }
}
