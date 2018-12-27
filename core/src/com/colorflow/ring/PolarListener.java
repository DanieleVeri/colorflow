package com.colorflow.ring;

import com.badlogic.gdx.InputAdapter;
import com.colorflow.utility.Position;

class PolarListener extends InputAdapter implements RingListener {

    private Ring ring;
    private Position actual, last;

    PolarListener(Ring ring) {
        this.ring = ring;
        this.actual = new Position.Pixel(0, 0);
        this.last = new Position.Pixel(0, 0);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        last.setX(screenX);
        last.setY(screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        actual.setX(screenX); actual.setY(screenY);
        ring.setRotation(ring.getRotation() + (last.getAngleRadial() - actual.getAngleRadial()) * ring.getSensibility());
        last.setX(screenX); last.setY(screenY);
        return false;
    }

    @Override
    public void onRingAct() {

    }
}