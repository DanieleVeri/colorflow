package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.MainGame
import com.colorflow.play.ring.Ring
import com.colorflow.utility.Position
import com.colorflow.utility.ButtonListener

class MenuScreen(private val game: MainGame) : Screen {
    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage: Stage
    private var ring: Ring? = null
    private val multiplexer: InputMultiplexer
    private var record: Label? = null

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = Stage(ScreenViewport(this.camera))
        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(this.stage)
        initUI()
    }

    override fun show() {
        record!!.setText("R3C0RD: " + game.dataManager.record)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        ring!!.rotateBy(1f)
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        ring!!.dispose()
        stage.dispose()
    }

    private fun initUI() {
        val table = Table()
        table.setFillParent(true)
        table.center().pad(30f)
        record = Label("R3C0RD: " + game.dataManager.record, game.assetProvider.getSkin("Menu"), "Title")
        ring = Ring(game.dataManager.usedRing)
        stage.addActor(this.ring)
        val title = Label("COLORFLOW", game.assetProvider.getSkin("Menu"), "Title")
        val playButton: ImageButton
        val shopButton: ImageButton
        playButton = ImageButton(game.assetProvider.getSkin("Menu"), "Play")
        shopButton = ImageButton(game.assetProvider.getSkin("Menu"), "Shop")
        playButton.addListener(object : ButtonListener(game.assetProvider) {
            override fun onTap() {
                game.screen = game.play
            }
        })
        shopButton.addListener(object : ButtonListener(game.assetProvider) {
            override fun onTap() {
                game.screen = game.shop
            }
        })
        table.add(title).expandX()
        table.add(shopButton).right()
        table.row()
        table.add(playButton).colspan(3).expand()
        table.row()
        table.add<Label>(record)
        stage.addActor(table)
    }
}
