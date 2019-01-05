package com.colorflow.play

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.screen.PlayScreen
import com.colorflow.screen.PlayScreen.State
import com.colorflow.utility.Position
import com.colorflow.utility.ButtonListener

import java.util.Observable
import java.util.Observer

class HUDStage(viewport: Viewport, private val playScreen: PlayScreen) : Stage(viewport), Observer {
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private var play: Table? = null
    private var pause: Table? = null
    private var over: Table? = null

    private val builder: StringBuilder = StringBuilder()
    private var scorePlay: Label? = null
    private var coinsPlay: Label? = null
    private var scorePause: Label? = null
    private var coinsPause: Label? = null
    private var scoreOver: Label? = null
    private var coinsOver: Label? = null

    init {
        initUI()
        setState(PlayScreen.State.PLAY)
    }

    override fun update(o: Observable, arg: Any?) {
        scorePlay!!.setText(playScreen.score.points.toString())
        builder.append(playScreen.score.coins).append("Z")
        coinsPlay!!.setText(builder)
        scorePause!!.setText(playScreen.score.points.toString())
        coinsPause!!.setText(builder)
        builder.delete(0, builder.length)
        if (playScreen.score.points <= playScreen.game.dataManager.record) {
            builder.append("SCORE: ").append(playScreen.score.points).append("\nRECORD: ")
                    .append(playScreen.game.dataManager.record)

        } else {
            builder.append("NEW R3C0RD!\n").append(playScreen.score.points)
        }
        scoreOver!!.setText(builder)
        builder.delete(0, builder.length)
        builder.append("COINS: ").append(playScreen.score.coins).append("Z")
        coinsOver!!.setText(builder)
        builder.delete(0, builder.length)
    }

    override fun draw() {
        if (playScreen.state != State.PLAY) {
            Gdx.graphics.gL20.glEnable(GL20.GL_BLEND)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(0f, 0f, 0f, 0.8f)
            shapeRenderer.rect(0f, 0f, Position.widthScreen, Position.heightScreen)
            shapeRenderer.end()
            Gdx.graphics.gL20.glDisable(GL20.GL_BLEND)
        }
        super.draw()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        super.dispose()
    }

    fun setState(state: State) {
        when (state) {
            PlayScreen.State.PLAY -> {
                play!!.isVisible = true
                pause!!.isVisible = false
                over!!.isVisible = false
            }
            PlayScreen.State.PAUSE -> {
                play!!.isVisible = false
                pause!!.isVisible = true
                over!!.isVisible = false
            }
            PlayScreen.State.OVER -> {
                play!!.isVisible = false
                pause!!.isVisible = false
                over!!.isVisible = true
            }
        }
    }

    private fun initUI() {
        val tablePad = Position.heightScreen / 48f
        scorePlay = Label("", playScreen.game.assetProvider.getSkin("Play"), "Score")
        coinsPlay = Label("", playScreen.game.assetProvider.getSkin("Play"), "Score")
        scorePause = Label("", playScreen.game.assetProvider.getSkin("Play"), "Score")
        coinsPause = Label("", playScreen.game.assetProvider.getSkin("Play"), "Score")
        scoreOver = Label("", playScreen.game.assetProvider.getSkin("Play"), "Score")
        coinsOver = Label("", playScreen.game.assetProvider.getSkin("Play"), "Score")
        val gameOver = Label("GAME OVER", playScreen.game.assetProvider.getSkin("Play"), "GameOver")
        val restartButton = ImageButton(playScreen.game.assetProvider.getSkin("Play"), "Redo")
        val adsButton = ImageButton(playScreen.game.assetProvider.getSkin("Play"), "Ads")
        val pauseButton = ImageButton(playScreen.game.assetProvider.getSkin("Play"), "Pause")
        val playButton = ImageButton(playScreen.game.assetProvider.getSkin("Play"), "Play")
        val homeButtonPause = ImageButton(playScreen.game.assetProvider.getSkin("Play"), "Home")
        val homeButtonOver = ImageButton(playScreen.game.assetProvider.getSkin("Play"), "Home")
        restartButton.addListener(object : ButtonListener(playScreen.game.assetProvider) {
            override fun onTap() {
                playScreen.reset()
            }
        })
        adsButton.addListener(object : ButtonListener(playScreen.game.assetProvider) {
            override fun onTap() {
                //TODO: Implement
            }
        })
        pauseButton.addListener(object : ButtonListener(playScreen.game.assetProvider) {
            override fun onTap() {
                if (playScreen.state == State.PLAY) {
                    playScreen.state = State.PAUSE
                }
            }
        })
        playButton.addListener(object : ButtonListener(playScreen.game.assetProvider) {
            override fun onTap() {
                playScreen.state = State.PLAY
            }
        })
        homeButtonPause.addListener(object : ButtonListener(playScreen.game.assetProvider) {
            override fun onTap() {
                playScreen.game.screen = playScreen.game.menu
            }
        })
        homeButtonOver.addListener(object : ButtonListener(playScreen.game.assetProvider) {
            override fun onTap() {
                playScreen.game.screen = playScreen.game.menu
            }
        })
        /* Play HUD */
        play = Table()
        play!!.setFillParent(true)
        play!!.pad(tablePad)
        play!!.top()
        play!!.add(pauseButton).expandX().left()
        play!!.add<Label>(scorePlay).expandX().right()
        play!!.row()
        play!!.add<Label>(coinsPlay).expand().colspan(2).bottom().right()
        addActor(play)
        /* Pause HUD */
        pause = Table()
        pause!!.setFillParent(true)
        pause!!.pad(tablePad)
        pause!!.top()
        pause!!.add<Label>(scorePause).colspan(2).expandX().right()
        pause!!.row()
        pause!!.add(playButton).expand()
        pause!!.add(homeButtonPause).expand()
        pause!!.row()
        pause!!.add<Label>(coinsPause).colspan(2).expandX().right()
        addActor(pause)
        /* Game Over HUD */
        over = Table()
        over!!.setFillParent(true)
        over!!.pad(tablePad)
        over!!.add(gameOver).colspan(2).expandX()
        over!!.row()
        over!!.add<Label>(scoreOver).expandX().left()
        over!!.add<Label>(coinsOver).expandX().right()
        over!!.row()
        over!!.add(adsButton).colspan(2).expand()
        over!!.row()
        over!!.add(restartButton).expand()
        over!!.add(homeButtonOver).expand()
        addActor(over)
    }

}