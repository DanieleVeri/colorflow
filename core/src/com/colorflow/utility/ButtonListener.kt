package com.colorflow.utility

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener

abstract class ButtonListener(private val assetProvider: AssetProvider) : ActorGestureListener() {

    override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
        assetProvider.getSound("button").play(1f)
        this.onTap()
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

    }

    protected abstract fun onTap()
}