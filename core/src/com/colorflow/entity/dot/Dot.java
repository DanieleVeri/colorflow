package com.colorflow.entity.dot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.colorflow.entity.Entity;
import com.colorflow.entity.Path;
import com.colorflow.entity.trigger.Trigger;
import com.colorflow.utility.Position;
import com.colorflow.utility.effect.ExplosionPool;

public class Dot extends Entity {

    private static final Texture stdTexture = new Texture("dots/std.png"),
                                reverseTexture = new Texture("dots/reverse.png"),
                                coinTexture = new Texture("dots/coin.png");
    protected Type type;
    protected Color color;

    public Dot() {
        super();
    }

    public void set(Type type, Color color, Path.Type pathType, Position.Radial start, float speed) {
        this.type = type;
        this.color = color;
        switch (type) {
            case STD:
                this.texture = stdTexture;
                break;
            case REVERSE:
                this.texture = reverseTexture;
                break;
            case COIN:
                this.texture = coinTexture;
                break;
            default:
                throw new IllegalStateException();
        }
        this.initTrail(color);
        this.path.set(pathType, start, speed);
        this.bounds.setRadius(40);
        super.set();
    }

    public Type getType() {
        return type;
    }

    public Color getColour() {
        return color;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(color.getRGB());
        super.draw(batch, parentAlpha);
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }

    @Override
    public void act(float delta) {
        rotateBy(1.5f);
        super.act(delta);
    }

    @Override
    public void destroy(Trigger trigger) {
        ExplosionPool.getInstance().start(getStage(), color.getRGB(), getPosition());
        DotPool.getInstance().free(this);
        super.destroy(trigger);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void reset() {
    }

    private void initTrail(Color color) {
        float[] colors = this.trail.getEmitters().first().getTint().getColors();
        colors[0] = color.getRGB().r;
        colors[1] = color.getRGB().g;
        colors[2] = color.getRGB().b;
    }

    public enum Type {
        STD, REVERSE, COIN
    }
}

