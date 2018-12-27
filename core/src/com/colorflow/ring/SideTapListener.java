package com.colorflow.ring;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.colorflow.utility.Position;

class SideTapListener extends InputAdapter implements RingListener {

    private Ring ring;
    private float rot;

    SideTapListener(Ring ring) {
        this.ring = ring;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        calcRot();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        calcRot();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        calcRot();
        return false;
    }

    @Override
    public void onRingAct() {
        ring.rotateBy(rot);
    }

    private void calcRot() {
        int clock = 0;
        for (int i = 0; i < 10; i++) {
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i) > Position.getCenter().getX()) {
                    clock++;
                } else {
                    clock --;
                }
            }
        }
        rot = clock * ring.getSensibility();
    }
}