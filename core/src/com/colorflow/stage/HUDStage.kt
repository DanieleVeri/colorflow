package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.graphic.Position
import com.colorflow.graphic.ButtonListener

class HUDStage(viewport: Viewport,
               protected val state: GameState,
               assets: AssetProvider) : Stage(viewport) {

    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private var play: Table
    private var pause: Table
    private var scorePlay: Label
    private var coinsPlay: Label
    private var scorePause: Label
    private var coinsPause: Label

    init {
        val tablePad = Position.heightScreen / 48f
        scorePlay = Label("", assets.get_skin("Play"), "Score")
        coinsPlay = Label("", assets.get_skin("Play"), "Score")
        scorePause = Label("", assets.get_skin("Play"), "Score")
        coinsPause = Label("", assets.get_skin("Play"), "Score")

        val pauseButton = ImageButton(assets.get_skin("Play"), "Pause")
        val playButton = ImageButton(assets.get_skin("Play"), "Play")
        val homeButtonFromPause = ImageButton(assets.get_skin("Play"), "Home")
        pauseButton.addListener(ButtonListener(assets) {
            state.current_game!!.paused = true
        })
        playButton.addListener(ButtonListener(assets) {
            state.current_game!!.paused = false
        })
        homeButtonFromPause.addListener(ButtonListener(assets) {
            state.set_screen(ScreenType.MENU)
        })

        // Play HUD
        play = Table()
        play.setFillParent(true)
        play.pad(tablePad)
        play.top()
        play.add(pauseButton).expandX().left()
        play.add(scorePlay).expandX().right()
        play.row()
        play.add(coinsPlay).expand().colspan(2).bottom().right()
        addActor(play)

        // Pause HUD
        pause = Table()
        pause.setFillParent(true)
        pause.pad(tablePad)
        pause.top()
        pause.add(scorePause).colspan(2).expandX().right()
        pause.row()
        pause.add(playButton).expand()
        pause.add(homeButtonFromPause).expand()
        pause.row()
        pause.add(coinsPause).colspan(2).expandX().right()
        addActor(pause)
    }

    override fun act(delta: Float) {
        scorePlay.setText(state.current_game!!.score.points)
        coinsPlay.setText(state.current_game!!.score.coins)
        scorePause.setText(state.current_game!!.score.points)
        coinsPause.setText(state.current_game!!.score.coins)

        pause.isVisible = state.current_game!!.paused
        play.isVisible = !state.current_game!!.paused

        super.act(delta)
    }

    override fun draw() {
        if(state.current_game!!.paused) {
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

}