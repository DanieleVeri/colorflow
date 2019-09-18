package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.utils.AssetProvider
import com.colorflow.persistence.IStorage
import com.colorflow.stage.ShopStage
import com.colorflow.utils.Position

class ShopScreen(
         persistence: IStorage,
         assets: AssetProvider) : Screen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage: ShopStage
    private val multiplexer: InputMultiplexer

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = ShopStage(ScreenViewport(this.camera), persistence, assets)
        this.multiplexer = InputMultiplexer()
        this.multiplexer.addProcessor(stage)
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

    override fun hide() {
    }

    override fun dispose() {
        stage.dispose()
    }

}