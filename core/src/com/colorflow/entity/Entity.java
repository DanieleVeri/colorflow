package com.colorflow.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.colorflow.entity.trigger.Trigger;
import com.colorflow.utility.Position;

public abstract class Entity extends Actor implements Pool.Poolable, Disposable {

    protected Texture texture;
    protected Circle bounds;
    protected Path path;
    protected ParticleEffect trail;

    protected Entity() {
        this.bounds = new Circle(0, 0, 1);
        this.path = new Path(Path.Type.RADIAL, new Position.Radial(1, 1), 1.5f);
        this.trail = new ParticleEffect(); trail.load(Gdx.files.internal("sprites/trail.p"), Gdx.files.internal("sprites"));
    }

    public void set() {
        setBounds(path.getPos().getX() - texture.getWidth() / 2,
                path.getPos().getY() - texture.getHeight() / 2,
                texture.getWidth(), texture.getHeight());
        bounds.setPosition(path.getPos().getX(), path.getPos().getY());
        trail.reset();
    }

    public Circle getBounds() {
        return bounds;
    }

    public Position.Radial getPosition() {
        return path.getPos();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        trail.draw(batch);
        batch.draw(texture, getX(), getY(),
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(), 1, 1, getRotation(), 0, 0, (int) getWidth(), (int) getHeight(), false, true);
    }

    @Override
    public void act(float delta) {
        /* Updating position */
        Position.Radial oldPos = getPosition();
        path.nextPos(delta);
        bounds.setPosition(path.getPos().getX(), path.getPos().getY());
        addAction(Actions.moveTo(
                path.getPos().getX() - texture.getWidth() / 2,
                path.getPos().getY() - texture.getHeight() / 2));
        /* Trail */
        trail.setPosition(getPosition().getX(), getPosition().getY());
        float angle = getPosition().getAngleRadial();
        trail.getEmitters().first().getAngle().setHigh(angle - 45, angle + 45);
        trail.getEmitters().first().getAngle().setLow(angle - 45, angle + 45);
        trail.flipY();
        trail.update(delta);
        /* Processing actions */
        super.act(delta);
        if (!isVisible()) {
            setVisible(true);
        }
    }

    public void destroy(Trigger trigger) {
        setVisible(false);
        if (trigger != null) {
            trigger.run(this);
        }
        super.remove();
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    @Override
    public void dispose() {
        trail.dispose();
    }

    @Override
    public abstract void reset();

}
