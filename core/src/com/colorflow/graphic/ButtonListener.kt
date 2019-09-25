package com.colorflow.graphic

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.colorflow.AssetProvider

class ButtonListener(private val assets: AssetProvider,
                     private val on_tap: ()->Unit) : ActorGestureListener() {

    override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
        assets.get_sound("button").play(1f)
        this.on_tap()
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

    }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {

    }
}