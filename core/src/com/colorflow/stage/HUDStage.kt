package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.graphic.Position
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.laction

class HUDStage(viewport: Viewport,
               protected val state: GameState,
               assets: AssetProvider) : Stage(viewport) {

    private val shapeRenderer: ShapeRenderer = ShapeRenderer()

    init {
        val score_play_label = Label("", assets.get_skin("ui"), "h2")
        val coins_play_label = Label("", assets.get_skin("ui"), "h2")
        val score_pause_label = Label("", assets.get_skin("ui"), "h2")
        val coins_pause_label = Label("", assets.get_skin("ui"), "h2")
        val pause_button = ImageButton(assets.get_skin("ui"), "pause")
        val play_button = ImageButton(assets.get_skin("ui"), "play")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
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
        val play = Table()
        play.width = Position.widthScreen
        play.height = Position.heightScreen
        play.top()
        play.add(pause_button).expandX().left()
        play.row()
        play.add(score_play_label).expandY().bottom().left()
        play.add(coins_play_label).expandY().bottom().right().padRight(15f)
        play.add(Image(TextureRegionDrawable(assets.get_skin("ui").getRegion("coin")))).expandY().bottom().right()
        addActor(play)

        // Pause HUD
        val pause = Table()
        pause.width = Position.widthScreen
        pause.height = Position.heightScreen
        pause.bottom()
        pause.add(Label("PAUSE", assets.get_skin("ui"), "h1")).colspan(2).expandX()
        pause.row()
        pause.add(play_button).expand()
        pause.add(Label("RESUME", assets.get_skin("ui"), "h2"))
        pause.row()
        pause.add(home_button).expand()
        pause.add(Label("EXIT", assets.get_skin("ui"), "h2"))
        pause.row()
        pause.add(score_pause_label)
        pause.add(coins_pause_label)
        pause.row()
        pause.add(Label("points", assets.get_skin("ui"), "h3"))
        pause.add(Image(TextureRegionDrawable(assets.get_skin("ui").getRegion("coin"))))
        addActor(pause)

        addAction(Actions.forever(laction {
            score_pause_label.setText(state.current_game!!.score.points)
            coins_pause_label.setText(state.current_game!!.score.coins)
            score_play_label.setText(state.current_game!!.score.points)
            coins_play_label.setText(state.current_game!!.score.coins)
            pause.isVisible = state.current_game!!.paused
            play.isVisible = !state.current_game!!.paused
        }))
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