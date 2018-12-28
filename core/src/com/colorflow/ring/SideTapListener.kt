package com.colorflow.ring

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.colorflow.utility.Position

internal class SideTapListener(private val ring: Ring) : InputAdapter(), RingListener {
    private var rot: Float = 0.toFloat()

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        calcRot()
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        calcRot()
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        calcRot()
        return false
    }

    override fun onRingAct() {
        ring.rotateBy(rot)
    }

    private fun calcRot() {
        var clock = 0
        for (i in 0..9) {
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i) > Position.center.x) {
                    clock++
                } else {
                    clock--
                }
            }
        }
        rot = clock * ring.sensibility
    }
}