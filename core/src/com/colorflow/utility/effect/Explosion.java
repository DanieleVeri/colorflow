package com.colorflow.utility.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.colorflow.utility.Position;

/**
 * Created by daniele on 01/08/17.
 */
public class Explosion extends Actor implements Disposable, Pool.Poolable {

    private ParticleEffect effect;

    public Explosion() {
        this.effect = new ParticleEffect();
        this.effect.load(Gdx.files.internal("sprites/exp.p"), Gdx.files.internal("sprites"));
    }

    public void start(Color color, Position position) {
        effect.reset();
        float[] colors = effect.getEmitters().first().getTint().getColors();
        colors[0] = color.r;
        colors[1] = color.g;
        colors[2] = color.b;
        effect.setPosition(position.getX(), position.getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
    }

    @Override
    public void act(float delta) {
        if (effect.isComplete()) {
            ExplosionPool.getInstance().free(this);
            this.remove();
        }
        effect.update(delta);
        super.act(delta);
    }

    @Override
    public void dispose() {
        effect.dispose();
    }

    @Override
    public void reset() {
    }
}
