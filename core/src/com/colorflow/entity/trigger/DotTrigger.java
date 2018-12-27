package com.colorflow.entity.trigger;

import com.badlogic.gdx.Gdx;
import com.colorflow.entity.Entity;
import com.colorflow.entity.dot.Dot;
import com.colorflow.ring.Ring;
import com.colorflow.screen.PlayScreen;
import com.colorflow.utility.Position;

class DotTrigger extends Trigger {

    public DotTrigger() {
    }

    @Override
    public void run(Entity entity) {
        Dot d = (Dot) entity;
        Ring ring = playStage.getRing();
        Position p = new Position.Pixel(0, 0);
        p.setX(d.getX() + d.getWidth() / 2.0f);
        p.setY(d.getY() + d.getHeight() / 2.0f);
        switch (d.getType()) {
            case STD:
                if (ring.getColorFor(p.getAngleRadial()).equals(d.getColour())) {
                    playStage.getPlayScreen().getScore().incPoints(10);
                } else {
                    Gdx.input.vibrate(200);
                    playStage.getPlayScreen().setState(PlayScreen.State.OVER);
                }
                break;
            case REVERSE:
                if (ring.getColorFor(p.getAngleRadial()).equals(d.getColour())) {
                    Gdx.input.vibrate(200);
                    playStage.getPlayScreen().setState(PlayScreen.State.OVER);
                } else {
                    playStage.getPlayScreen().getScore().incPoints(10);
                }
                break;
            case COIN:
                if (ring.getColorFor(p.getAngleRadial()).equals(d.getColour())) {
                    playStage.getPlayScreen().getScore().incCoins(1);
                    playStage.getPlayScreen().getScore().incPoints(10);
                } else {
                    Gdx.input.vibrate(200);
                    playStage.getPlayScreen().setState(PlayScreen.State.OVER);
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
