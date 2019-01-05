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
import com.colorflow.ring.Ring
import com.colorflow.utility.ButtonListener
import com.colorflow.utility.Position

import java.util.Observable
import java.util.Observer

class ShopScreen(private val game: MainGame) : Screen, Observer {
    private var tab: Tab? = null
    private val camera: OrthographicCamera = OrthographicCamera()
    private val stage: Stage
    private val multiplexer: InputMultiplexer
    /* UI */
    private var dataVersion = ""
    private var table: Table? = null
    private var homeBtn: ImageButton? = null
    private var ringBtn: ImageButton? = null
    private var bonusBtn: ImageButton? = null
    private var title: Label? = null
    private var coins: Label? = null
    private var ringScroll: ScrollPane? = null
    private var bonusScroll: ScrollPane? = null
    private var ringTable: Table? = null
    private var bonusTable: Table? = null

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.stage = Stage(ScreenViewport(this.camera))
        this.multiplexer = InputMultiplexer()
        this.multiplexer.addProcessor(stage)
        initUI()
        loadContent()
        setTab(Tab.RINGS)
    }

    override fun show() {
        game.dataManager.addObserver(this)
        Gdx.input.inputProcessor = multiplexer
        loadContent()
        updateStatus()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {
        game.dataManager.deleteObserver(this)
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun update(o: Observable, arg: Any) {
        updateStatus()
    }

    private fun initUI() {
        table = Table()
        table!!.setFillParent(true)
        table!!.pad(30f)
        homeBtn = ImageButton(game.assetProvider.getSkin("Shop"), "Home")
        homeBtn!!.addListener(object : ButtonListener(game.assetProvider) {
            override fun onTap() {
                game.screen = game.menu
            }
        })
        ringBtn = ImageButton(game.assetProvider.getSkin("Shop"), "Ring")
        ringBtn!!.addListener(object : ButtonListener(game.assetProvider) {
            override fun onTap() {
                if (tab != Tab.RINGS) {
                    setTab(Tab.RINGS)
                }
            }
        })
        bonusBtn = ImageButton(game.assetProvider.getSkin("Shop"), "Bonus")
        bonusBtn!!.addListener(object : ButtonListener(game.assetProvider) {
            override fun onTap() {
                if (tab != Tab.BONUS) {
                    setTab(Tab.BONUS)
                }
            }
        })
        ringTable = Table()
        bonusTable = Table()
        ringScroll = ScrollPane(ringTable)
        bonusScroll = ScrollPane(bonusTable)
        title = Label("--title--", game.assetProvider.getSkin("Shop"), "Title")
        coins = Label("--coins--", game.assetProvider.getSkin("Shop"), "Coins")
        table!!.add<ImageButton>(homeBtn)
        table!!.add<Label>(title)
        table!!.row()
        table!!.add<ImageButton>(ringBtn)
        table!!.add<ScrollPane>(ringScroll).expand().fill().right()
        table!!.row()
        table!!.add<ImageButton>(bonusBtn)
        table!!.add<Label>(coins)
        stage.addActor(table)
    }

    private fun loadContent() {
        if (dataVersion == game.dataManager.version) {
            return
        }
        dataVersion = game.dataManager.version
        /* Rings */
        val files = Gdx.files.local("rings").list()
        ringTable!!.clear()
        for (i in files.indices) {
            if (files[i].extension() == "xml") {
                val ring = Ring(files[i].name())
                ring.addAction(Actions.rotateBy(99999999f, 1999999f))
                val name = Label(ring.name, game.assetProvider.getSkin("Shop"), "ItemName")
                val cost = TextButton(ring.cost.toString() + "Z", game.assetProvider.getSkin("Shop"), "Buy")
                cost.addListener(object : ButtonListener(game.assetProvider) {
                    override fun onTap() {
                        game.dataManager.purchaseRing(ring.cost, ring.id)
                        cost.isDisabled = true
                        cost.touchable = Touchable.disabled
                    }
                })
                ringTable!!.add(ring).expandX()
                ringTable!!.add(name).expandX()
                ringTable!!.add(cost).expandX()
                ringTable!!.row().pad(20f)
            }
        }
        /* Bonus */
    }

    private fun updateStatus() {
        coins!!.setText(game.dataManager.coins.toString() + "Z")
        /* Ring */
        var ring: Ring? = null
        var button: TextButton
        for (c in ringTable!!.cells) {
            if (c.actor is Ring) {
                ring = c.actor as Ring
            }
            if (c.actor is TextButton) {
                button = c.actor as TextButton
                if (game.dataManager.coins < ring!!.cost || game.dataManager.unlockedRings.contains(ring!!.id)) {
                    button.isDisabled = true
                    button.touchable = Touchable.disabled
                }
            }
        }
        /* Bonus */
    }

    private fun setTab(tab: Tab) {
        if (this.tab == null) {
            render(Gdx.graphics.deltaTime)
        }
        title!!.setText(tab.toString())
        when (tab) {
            ShopScreen.Tab.RINGS -> {
                table!!.cells.get(3).setActor<ScrollPane>(ringScroll)
                ringBtn!!.addAction(Actions.moveBy(50f, 0f, 0.25f))
            }
            ShopScreen.Tab.BONUS -> {
                table!!.cells.get(3).setActor<ScrollPane>(bonusScroll)
                bonusBtn!!.addAction(Actions.moveBy(50f, 0f, 0.25f))
            }
        }
        if (this.tab != null) {
            when (this.tab) {
                ShopScreen.Tab.RINGS -> ringBtn!!.addAction(Actions.moveBy(-20f, 0f, 0.25f))
                ShopScreen.Tab.BONUS -> bonusBtn!!.addAction(Actions.moveBy(-20f, 0f, 0.25f))
                else -> throw IllegalStateException()
            }
        }
        this.tab = tab
    }

    internal enum class Tab {
        RINGS, BONUS
    }
}