package com.colorflow.ring

import com.badlogic.gdx.InputProcessor

internal interface RingListener : InputProcessor {
    fun onRingAct()
}
