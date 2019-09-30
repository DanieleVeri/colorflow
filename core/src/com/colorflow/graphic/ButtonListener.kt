package com.colorflow.graphic

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.colorflow.AssetProvider

open class ButtonListener(protected val assets: AssetProvider,
                     protected val button: Button,
                     protected val on_tap: ()->Unit) : ActorGestureListener() {

    override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
        if (this.button.isDisabled) {
            assets.get_sound("disabled").play(1f)
            return
        }
        assets.get_sound("click").play(1f)
        this.on_tap()
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) { }

    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) { }
}