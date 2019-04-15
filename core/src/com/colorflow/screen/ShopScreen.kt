package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.MainGame
import com.colorflow.play.ring.Ring
import com.colorflow.utility.ButtonListener
import com.colorflow.utility.Position

import java.util.Observable
import java.util.Observer

class ShopScreen(private val game: MainGame) : Screen, Observer {
    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage: Stage
    private val multiplexer: InputMultiplexer
    /* UI */
    private var dataVersion = ""
    private var table: Table = Table()
    private var homeBtn: ImageButton? = null
    private var coins: Label = Label("", game.assets.getSkin("Shop"), "Coins")
    private var ringScroll: ScrollPane? = null
    private var ringList: Table = Table()

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = Stage(ScreenViewport(this.camera))
        this.multiplexer = InputMultiplexer()
        this.multiplexer.addProcessor(stage)
        initUI()
        loadContent()
    }

    override fun show() {
        game.persistence.addObserver(this)
        Gdx.input.inputProcessor = multiplexer
        loadContent()
        updateStatus()
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

    override fun hide() {
        game.persistence.deleteObserver(this)
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun update(o: Observable, arg: Any) {
        updateStatus()
    }

    private fun initUI() {
        table.setFillParent(true)
        table.pad(30f)
        homeBtn = ImageButton(game.assets.getSkin("Shop"), "Home")
        homeBtn!!.addListener(object : ButtonListener(game.assets) {
            override fun onTap() {
                game.screen = game.menu
            }
        })

        ringScroll = ScrollPane(ringList)
        val title = Label("Have a lucky day", game.assets.getSkin("Shop"), "Title")
        table.add<ImageButton>(homeBtn).left()
        table.add<Label>(title)
        table.row()
        table.add<ScrollPane>(ringScroll).expand().fill().colspan(2)
        table.row()
        table.add<Label>(coins).colspan(2)
        stage.addActor(table)
    }

    private fun loadContent() {
        if (dataVersion == game.persistence.version) {
            return
        }
        dataVersion = game.persistence.version
        /* Rings */
        val files = Gdx.files.local("rings").list()
        ringList.clear()
        for (i in files.indices) {
            if (files[i].extension() == "xml") {
                val ring = Ring(files[i].name())
                ring.addAction(Actions.rotateBy(99999999f, 1999999f))
                val name = Label(ring.name, game.assets.getSkin("Shop"), "ItemName")
                val cost = TextButton(ring.cost.toString(), game.assets.getSkin("Shop"), "Buy")
                cost.addListener(object : ButtonListener(game.assets) {
                    override fun onTap() {
                        game.persistence.purchaseRing(ring.cost, ring.id)
                        cost.isDisabled = true
                        cost.touchable = Touchable.disabled
                    }
                })
                ringList.add(ring).expandX()
                ringList.add(name).expandX()
                ringList.add(cost).expandX()
                ringList.row().pad(20f)
            }
        }
    }

    private fun updateStatus() {
        val coin = game.persistence.coins
        coins.setText(coin.toString() + if (coin == 1) " coin" else " coins")
        /* Ring */
        var ring: Ring? = null
        var button: TextButton
        for (c in ringList.cells) {
            if (c.actor is Ring) {
                ring = c.actor as Ring
            }
            if (c.actor is TextButton) {
                button = c.actor as TextButton
                if (game.persistence.coins < ring!!.cost || game.persistence.unlockedRings.contains(ring.id)) {
                    button.isDisabled = true
                    button.touchable = Touchable.disabled
                }
            }
        }
    }

}