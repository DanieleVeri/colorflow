package com.colorflow.graphic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.AssetProvider
import com.colorflow.state.GameState

abstract class UiScreen<T : Stage>(
        protected var state: GameState,
        protected var assets: AssetProvider) : Screen {

    protected val camera: OrthographicCamera
    protected val viewport: Viewport
    protected val multiplexer: InputMultiplexer
    protected lateinit var stage: T

    init {
        camera = OrthographicCamera()
        camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        camera.update()
        viewport = ScreenViewport(this.camera)
        multiplexer = InputMultiplexer()
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() { stage.dispose() }
}