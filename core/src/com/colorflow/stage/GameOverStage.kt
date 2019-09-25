package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.AssetProvider
import com.colorflow.state.CurrentGame
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.Position

class GameOverStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad_handler: IAdHandler): Stage(viewport) {

    protected val score: Label
    protected val coins: Label
    protected val ad_button: ImageButton

    private val table: Table = Table()

    init {
        score = Label("", assets.get_skin("Play"), "Score")
        coins = Label("", assets.get_skin("Play"), "Score")

        val title = Label("GAME OVER", assets.get_skin("Play"), "GameOver")
        val restart_button = ImageButton(assets.get_skin("Play"), "Redo")
        ad_button = ImageButton(assets.get_skin("Play"), "Ads")
        val home_button = ImageButton(assets.get_skin("Play"), "Home")

        restart_button.addListener(ButtonListener(assets) {
            save_result()
            state.current_game = CurrentGame(state.current_game!!.selected_track)
            state.set_screen(ScreenType.LOAD)
        })
        ad_button.addListener(ButtonListener(assets) {
            ad_handler.show_ad()
        })
        home_button.addListener(ButtonListener(assets) {
            save_result()
            state.current_game = null
            state.set_screen(ScreenType.MENU)
        })

        val table_pad = Position.heightScreen / 48f
        table.setFillParent(true)
        table.pad(table_pad)
        table.add(title).colspan(2).expandX()
        table.row()
        table.add(score).expandX().left()
        table.add(coins).expandX().right()
        table.row()
        table.add(ad_button).colspan(2).expand()
        table.row()
        table.add(restart_button).expand()
        table.add(home_button).expand()
        addActor(table)
    }

    fun update() {
        ad_button.touchable = Touchable.enabled

        if (state.current_game!!.score.points <= state.record)
            score.setText("SCORE: " + state.current_game!!.score.points + "\nRECORD: " + state.record)
        else
            score.setText("NEW RECORD!\n" + state.current_game!!.score.points)
        coins.setText("COINS: " + state.current_game!!.score.coins)
    }

    fun reward() {
        ad_button.touchable = Touchable.disabled

        state.current_game!!.score.coins *= 2
        save_result()
        coins.setText("COINS: " + state.current_game!!.score.coins)
    }

    protected fun save_result() {
        state.coins += state.current_game!!.score.coins
        if (state.current_game!!.score.points > state.record)
            state.record = state.current_game!!.score.points
        state.persist()
    }

    override fun act(delta: Float) {
        super.act(delta)
    }

}