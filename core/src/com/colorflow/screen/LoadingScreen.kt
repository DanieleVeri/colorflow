package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.utility.Position

class LoadingScreen : Screen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage: Stage
    private var t = -Math.PI / 2

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = Stage(ScreenViewport(this.camera))
    }

    override fun show() {}

    override fun render(delta: Float) {
        t += Math.PI * delta
        Gdx.gl.glClearColor((Math.sin(t) / 2 + 0.5).toFloat(),
                (Math.sin(t) / 2 + 0.5).toFloat(),
                (Math.sin(t) / 2 + 0.5).toFloat(), 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }
}
