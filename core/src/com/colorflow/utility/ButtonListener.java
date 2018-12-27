package com.colorflow.utility;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

public abstract class ButtonListener extends ActorGestureListener {

    private AssetProvider assetProvider;

    public ButtonListener(AssetProvider assetProvider) {
        this.assetProvider = assetProvider;
    }

    @Override
    public void tap(InputEvent event, float x, float y, int count, int button) {
        assetProvider.getSound("button").play(1);
        this.onTap();
    }

    @Override
    public void touchDown(InputEvent event, float x, float y, int pointer, int button) {

    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

    }

    protected abstract void onTap();
}