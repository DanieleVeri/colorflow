package com.colorflow.play;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colorflow.entity.bonus.Bonus;
import com.colorflow.entity.bonus.BonusPool;
import com.colorflow.entity.dot.Dot;
import com.colorflow.entity.dot.DotPool;
import com.colorflow.entity.trigger.Trigger;
import com.colorflow.ring.Ring;
import com.colorflow.screen.PlayScreen;
import com.colorflow.utility.effect.Explosion;
import com.colorflow.utility.effect.ExplosionPool;


public class PlayStage extends Stage {

    private PlayScreen playScreen;
    private Ring ring;
    private Spawner spawner;
    private boolean isPlaying = true;
    private float timer = 0;

    public PlayStage(Viewport viewport, PlayScreen playScreen) {
        super(viewport);
        this.playScreen = playScreen;
        this.spawner = new Spawner(this);
    }

    public PlayScreen getPlayScreen() {
        return playScreen;
    }

    public Ring getRing() {
        return ring;
    }

    public void setRing(Ring ring) {
        this.ring = ring;
        addActor(ring);
    }

    public void setState(PlayScreen.State state) {
        if (state != PlayScreen.State.PLAY) {
            isPlaying = false;
        } else {
            isPlaying = true;
        }
    }

    public void reset() {
        for (Actor a : getActors()) {
            if (a instanceof Dot) {
                DotPool.getInstance().free((Dot) a);
            }
            if (a instanceof Bonus) {
                BonusPool.getInstance().free((Bonus) a);
            }
            if (a instanceof Explosion) {
                ExplosionPool.getInstance().free((Explosion) a);
            }
        }
        if (ring != null) ring.dispose();
        clear();
        setRing(new Ring(playScreen.getGame().getDataManager().getUsedRing()));
        spawner.reset();
        isPlaying = true;
    }

    @Override
    public void act(float delta) {
        if (!isPlaying) {
            return;
        }
        detectCollision();
        spawn(delta);
        super.act(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        ring.dispose();
        DotPool.getInstance().clear();
        BonusPool.getInstance().clear();
        ExplosionPool.getInstance().clear();
    }

    private void spawn(float delta) {
        timer += delta;
        /* Dots */
        if (timer > 2) {
            timer = 0;
            spawner.waveDotMix(4);
        }
        /* Bonus */
        if (Math.random() < 0.0005) {
            spawner.bonus();
        }
    }

    private void detectCollision() {
        for (Actor a : getActors()) {
            if (a instanceof Bonus) {
                Bonus b = (Bonus) a;
                if (Intersector.overlaps(b.getBounds(), ring.getCircle())) {
                    b.destroy(Trigger.getBonus(this));
                }
            }
        }
        for (Actor a : getActors()) {
            if (a instanceof Dot) {
                Dot d = (Dot) a;
                if (Intersector.overlaps(d.getBounds(), ring.getCircle())) {
                    d.destroy(Trigger.getDot(this));
                }
            }
        }
    }

}
