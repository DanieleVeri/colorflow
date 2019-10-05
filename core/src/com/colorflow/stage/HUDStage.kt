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
    private var score_play_label: Label
    private var coins_play_label: Label
    private var score_pause_label: Label
    private var coins_pause_label: Label

    init {
        val tablePad = Position.heightScreen / 48f
        score_play_label = Label("", assets.get_skin("Play"), "Score")
        coins_play_label = Label("", assets.get_skin("Play"), "Score")
        score_pause_label = Label("", assets.get_skin("Play"), "Score")
        coins_pause_label = Label("", assets.get_skin("Play"), "Score")

        val pause_button = ImageButton(assets.get_skin("Play"), "Pause")
        val play_button = ImageButton(assets.get_skin("Play"), "Play")
        val home_button = ImageButton(assets.get_skin("Play"), "Home")
        pause_button.addListener(ButtonListener(assets, pause_button) {
            state.current_game!!.paused = true
        })
        play_button.addListener(ButtonListener(assets, play_button) {
            state.current_game!!.paused = false
        })
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })

        // Play HUD
        play = Table()
        play.setFillParent(true)
        play.pad(tablePad)
        play.top()
        play.add(pause_button).expandX().left()
        play.add(score_play_label).expandX().right()
        play.row()
        play.add(coins_play_label).expand().colspan(2).bottom().right()
        addActor(play)

        // Pause HUD
        pause = Table()
        pause.setFillParent(true)
        pause.pad(tablePad)
        pause.top()
        pause.add(score_pause_label).colspan(2).expandX().right()
        pause.row()
        pause.add(play_button).expand()
        pause.add(home_button).expand()
        pause.row()
        pause.add(coins_pause_label).colspan(2).expandX().right()
        addActor(pause)
    }

    override fun act(delta: Float) {
        score_pause_label.setText(state.current_game!!.score.points)
        coins_pause_label.setText(state.current_game!!.score.coins)
        score_play_label.setText(state.current_game!!.score.points)
        coins_play_label.setText(state.current_game!!.score.coins)
        pause.isVisible = state.current_game!!.paused
        play.isVisible = !state.current_game!!.paused
        super.act(delta)
    }

    override fun draw() {
        if(state.current_game!!.paused) {
            Gdx.graphics.gL20.glEnable(GL20.GL_BLEND)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer.setColor(0f, 0f, 0f, 0.75f)
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