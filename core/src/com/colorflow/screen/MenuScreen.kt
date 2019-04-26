package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.MainGame
import com.colorflow.ScreenManager
import com.colorflow.ScreenType
import com.colorflow.play.ring.Ring
import com.colorflow.persistence.AssetProvider
import com.colorflow.persistence.IStorage
import com.colorflow.stage.MenuStage
import com.colorflow.utils.Position
import com.colorflow.utils.ButtonListener

class MenuScreen(private val game: MainGame,
                 private val persistence: IStorage,
                 private val assets: AssetProvider) : Screen {
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

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
