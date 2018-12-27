package com.colorflow.entity.trigger;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.colorflow.entity.Entity;
import com.colorflow.entity.bonus.Bonus;
import com.colorflow.entity.dot.Dot;
import com.colorflow.utility.effect.ExplosionPool;

public class BonusTrigger extends Trigger {

    public BonusTrigger() {
        super();
    }

    @Override
    public void run(Entity entity) {
        Bonus b = (Bonus) entity;
        switch (b.getType()) {
            case BOMB:
                Array<Actor> list = playStage.getActors();
                for (int i = 0; i < list.size; i++) {
                    if (list.get(i) instanceof Dot) {
                        Dot d = (Dot) list.get(i);
                        d.addAction(Actions.removeActor());
                        ExplosionPool.getInstance().start(playStage, d.getColour().getRGB(), d.getPosition());
                    }
                }
                playStage.getPlayScreen().getScore().incPoints(400);
                break;
            default:
                throw new IllegalStateException();
        }

    }
}
