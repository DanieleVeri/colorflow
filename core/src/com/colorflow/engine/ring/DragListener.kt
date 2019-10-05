package com.colorflow.engine.ring

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.colorflow.graphic.Position

class DragListener : InputAdapter(), RingListener {
    private var angle: Float = 0f
    private var pointers = Array(10) {-1}
    private var sensibility = 360f / Position.widthScreen

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(pointer >= pointers.size) return false
        pointers[pointer] = screenX
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if(pointer >= pointers.size) return false
        angle = (screenX - pointers[pointer]) * sensibility
        pointers[pointer] = screenX
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(pointer >= pointers.size) return false
        pointers[pointer] = -1
        if(pointers.all { it == -1 }) {
            angle = 0f
        }
        return false
    }

    override fun on_ring_act(): Action {
        return Actions.rotateBy(angle)
    }

}