package com.colorflow.entity.bonus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.colorflow.entity.Entity;
import com.colorflow.entity.Path;
import com.colorflow.entity.trigger.Trigger;
import com.colorflow.utility.Position;
import com.colorflow.utility.effect.ExplosionPool;

public class Bonus extends Entity {

    private static final Texture bombTexture = new Texture("bonus/bomb.png");
    protected Type type;

    public Bonus() {
        super();
    }

    public void set(Type type, Path.Type pathType, Position.Radial start, float speed) {
        this.type = type;
        float[] colors = this.trail.getEmitters().first().getTint().getColors();
        colors[0] = 1;
        colors[1] = 1;
        colors[2] = 1;
        switch (type) {
            case BOMB:
                this.texture = bombTexture;
                break;
            default:
                throw new IllegalStateException();
        }
        this.path.set(pathType, start, speed);
        this.bounds.setRadius(40);
        super.set();
    }

    public Type getType() {
        return type;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        super.draw(batch, alpha);
    }

    @Override
    public void act(float delta) {
        rotateBy(1.5f);
        super.act(delta);
    }

    @Override
    public void destroy(Trigger trigger) {
        ExplosionPool.getInstance().start(getStage(), Color.WHITE, getPosition());
        BonusPool.getInstance().free(this);
        super.destroy(trigger);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void reset() {
    }

    public enum Type {
        BOMB, GOLD, MAGNETIC
    }
}
