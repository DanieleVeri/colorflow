package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.ScreenManager
import com.colorflow.ScreenType
import com.colorflow.screen.PlayScreen
import com.colorflow.screen.PlayScreen.State
import com.colorflow.utils.AssetProvider
import com.colorflow.play.Score
import com.colorflow.utils.Position
import com.colorflow.utils.ButtonListener

class HUDStage(viewport: Viewport,
               assets: AssetProvider,
               private val score: Score,
               private val play_screen: PlayScreen) : Stage(viewport) {

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
        val tablePad = Position.heightScreen / 48f
        scorePlay = Label("", assets.get_skin("Play"), "Score")
        coinsPlay = Label("", assets.get_skin("Play"), "Score")
        scorePause = Label("", assets.get_skin("Play"), "Score")
        coinsPause = Label("", assets.get_skin("Play"), "Score")
        scoreOver = Label("", assets.get_skin("Play"), "Score")
        coinsOver = Label("", assets.get_skin("Play"), "Score")

        val gameOver = Label("GAME OVER", assets.get_skin("Play"), "GameOver")
        val restartButton = ImageButton(assets.get_skin("Play"), "Redo")
        val adsButton = ImageButton(assets.get_skin("Play"), "Ads")
        val pauseButton = ImageButton(assets.get_skin("Play"), "Pause")
        val playButton = ImageButton(assets.get_skin("Play"), "Play")
        val homeButtonFromPause = ImageButton(assets.get_skin("Play"), "Home")
        val homeButtonFromOver = ImageButton(assets.get_skin("Play"), "Home")

        restartButton.addListener(ButtonListener(assets) {
            play_screen.reset()
        })
        adsButton.addListener(ButtonListener(assets) {
            //TODO: Implement
        })
        pauseButton.addListener(ButtonListener(assets) {
            if (play_screen.state == State.PLAY)
                play_screen.state = State.PAUSE
        })
        playButton.addListener(ButtonListener(assets) {
            play_screen.state = State.PLAY
        })
        homeButtonFromPause.addListener(ButtonListener(assets) {
            ScreenManager.set(ScreenType.MENU)
        })
        homeButtonFromOver.addListener(ButtonListener(assets) {
            ScreenManager.set(ScreenType.MENU)
        })

        // Play HUD
        play = Table()
        play!!.setFillParent(true)
        play!!.pad(tablePad)
        play!!.top()
        play!!.add(pauseButton).expandX().left()
        play!!.add<Label>(scorePlay).expandX().right()
        play!!.row()
        play!!.add<Label>(coinsPlay).expand().colspan(2).bottom().right()
        addActor(play)
        // Pause HUD
        pause = Table()
        pause!!.setFillParent(true)
        pause!!.pad(tablePad)
        pause!!.top()
        pause!!.add<Label>(scorePause).colspan(2).expandX().right()
        pause!!.row()
        pause!!.add(playButton).expand()
        pause!!.add(homeButtonFromPause).expand()
        pause!!.row()
        pause!!.add<Label>(coinsPause).colspan(2).expandX().right()
        addActor(pause)
        // Game Over HUD
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
        over!!.add(homeButtonFromOver).expand()
        addActor(over)
        setState(PlayScreen.State.PLAY)
    }

    override fun act(delta: Float) {
        scorePlay!!.setText(score.points.toString())
        builder.append(score.coins)
        coinsPlay!!.setText(builder)
        scorePause!!.setText(score.points.toString())
        coinsPause!!.setText(builder)
        builder.delete(0, builder.length)
        if (score.points <= score.record) {
            builder.append("SCORE: ").append(score.points).append("\nRECORD: ").append(score.record)
        } else {
            builder.append("NEW RECORD!\n").append(score.points)
        }
        scoreOver!!.setText(builder)
        builder.delete(0, builder.length)
        builder.append("COINS: ").append(score.coins)
        coinsOver!!.setText(builder)
        builder.delete(0, builder.length)
        super.act(delta)
    }

    override fun draw() {
        if (play_screen.state != State.PLAY) {
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

}