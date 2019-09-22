package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.utils.AssetProvider
import com.colorflow.os.IStorage
import com.colorflow.stage.MenuStage
import com.colorflow.utils.Position

class MenuScreen(persistence: IStorage,
                 assets: AssetProvider) : Screen {
    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage: MenuStage
    private val multiplexer: InputMultiplexer

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = MenuStage(ScreenViewport(this.camera), persistence, assets)
        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(this.stage)
    }

    override fun show() {
        Gdx.input.inputProcessor = multiplexer
        stage.sync_from_storage()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.1f, .1f, .1f, .1f)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        stage.dispose()
    }

}
