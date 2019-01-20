package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
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
    private val multiplexer: InputMultiplexer

    private var record: Label? = null
    private var coins: Label? = null
    private var play_button: Button? = null
    private var slot_button: Button? = null
    private var dy = 0.0
    private var ring: Ring? = null
    private val renderer = ShapeRenderer()

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = Stage(ScreenViewport(this.camera))
        multiplexer = InputMultiplexer()
        multiplexer.addProcessor(this.stage)
        initUI()
    }

    override fun show() {
        record?.setText("REC0RD: " + game.persistence.record)
        coins?.setText("COINS: " + game.persistence.coins)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(.1f, .1f, .1f, .1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        //renderer.rect(0f, 0f, Position.widthScreen, Position.heightScreen, Color.RED, Color.GREEN, Color.BLUE, Color.VIOLET)
        renderer.end()

        ring?.rotateBy(1f)
        play_button?.moveBy(0f, Math.sin(dy).toFloat() / 2)
        slot_button?.moveBy(0f, Math.sin(1+dy).toFloat() / 2)

        stage.act(Gdx.graphics.deltaTime)
        stage.draw()

        dy += delta.toDouble()
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
        stage.dispose()
        ring?.dispose()
    }

    private fun initUI() {
        /* Creating items */
        val title = Label("COLORFLOW", game.assets.getSkin("Menu"), "Title")
        record = Label("REC0RD: " + game.persistence.record, game.assets.getSkin("Menu"), "Menu")
        coins = Label("COINS: " + game.persistence.coins, game.assets.getSkin("Menu"), "Menu")
        play_button = ImageButton(game.assets.getSkin("Menu"), "Play")
        slot_button = ImageButton(game.assets.getSkin("Menu"), "Slot")
        play_button!!.addListener(object : ButtonListener(game.assets) {
            override fun onTap() {
                game.screen = game.play
            }
        })
        slot_button!!.addListener(object : ButtonListener(game.assets) {
            override fun onTap() {
                game.screen = game.shop
            }
        })
        ring = Ring(game.persistence.usedRing)
        /* Positioning */
        val table = Table()
        table.setFillParent(true)
        table.top()

        table.add(title).colspan(2).expandY()
        table.row()
        table.add(ring).colspan(2).expand()
        table.row()
        table.add(play_button).expandY()
        table.add<Label>(record).expandY()
        table.row()
        table.add(slot_button).expandY()
        table.add<Label>(coins).expandY()

        stage.addActor(table)
    }
}
