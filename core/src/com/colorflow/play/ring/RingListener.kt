package com.colorflow.play.ring

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Action

internal interface RingListener : InputProcessor {
    fun on_ring_act(): Action
}
