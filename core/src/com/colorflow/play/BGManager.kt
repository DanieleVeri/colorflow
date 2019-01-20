package com.colorflow.play

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20

class BGManager(private val playStage: PlayStage) {
    var bgColor: Color = Color.BLACK

    fun render() {
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    fun reset() {
        bgColor = Color.BLACK
    }
}