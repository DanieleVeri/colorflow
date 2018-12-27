package com.colorflow.utility.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.colorflow.utility.Position;

/**
 * Created by daniele on 09/05/17.
 */

public class ExplosionPool extends Pool<Explosion> {

    private static ExplosionPool instance = null;

    private ExplosionPool() {
    }

    @Override
    protected Explosion newObject() {
        return new Explosion();
    }

    public static ExplosionPool getInstance() {
        if (instance == null) {
            instance = new ExplosionPool();
        }
        return instance;
    }

    public void start(Stage stage, Color color, Position position) {
        Explosion e = obtain();
        stage.addActor(e);
        e.start(color, position);
    }

}

