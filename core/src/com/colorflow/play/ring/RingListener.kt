package com.colorflow.play.ring

import com.badlogic.gdx.InputProcessor

internal interface RingListener : InputProcessor {
    fun onRingAct()
}
