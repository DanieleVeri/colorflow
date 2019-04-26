package com.colorflow.play.ring

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

internal class SideTapListener : InputAdapter(), RingListener {
    private var rot: Float = 0f
    private var last_x: Int = -1

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        rot = if (last_x == -1) 0f else (screenX - last_x) / 7f
        last_x = screenX
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        rot = if (last_x == -1) 0f else (screenX - last_x) / 7f
        last_x = screenX
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        rot = 0f
        last_x = -1
        return false
    }

    override fun onRingAct(): Action {
        return Actions.rotateBy(rot)
    }

}