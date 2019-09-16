package com.colorflow.play

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor

class BackgroundManager: Actor() {
    var bgColor: Color = Color.BLACK

    override fun act(delta: Float) {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.act(delta)
    }

    fun reset() {
        bgColor = Color.BLACK
    }
}