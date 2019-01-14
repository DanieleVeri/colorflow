package com.colorflow.play.ring

import com.badlogic.gdx.InputAdapter
import com.colorflow.utility.Position

internal class PolarListener(private val ring: Ring) : InputAdapter(), RingListener {
    private val actual: Position
    private val last: Position

    init {
        this.actual = Position.Pixel(0f, 0f)
        this.last = Position.Pixel(0f, 0f)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        last.x = screenX.toFloat()
        last.y = screenY.toFloat()
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        actual.x = screenX.toFloat()
        actual.y = screenY.toFloat()
        ring.rotation = ring.rotation + (last.angleRadial - actual.angleRadial) * ring.sensibility
        last.x = screenX.toFloat()
        last.y = screenY.toFloat()
        return false
    }

    override fun onRingAct() {

    }
}