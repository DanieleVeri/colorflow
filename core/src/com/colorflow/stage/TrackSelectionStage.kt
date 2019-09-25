package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.AssetProvider
import com.colorflow.CurrentGame
import com.colorflow.GameState
import com.colorflow.ScreenType
import com.colorflow.os.IAdHandler
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
import com.colorflow.utils.ButtonListener
import kotlin.concurrent.thread

class TrackSelectionStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad_handler: IAdHandler,
        protected val music_manager: IMusicManager,
        protected val music_analyzer: IMusicAnalyzer): Stage(viewport) {

    init {
        val home_button = ImageButton(assets.get_skin("Shop"), "Home")
        home_button.addListener(ButtonListener(assets) { state.set_screen(ScreenType.MENU) })

        val track_label = Label("Track: 0", assets.get_skin("Menu"), "Title")
        val play_button = ImageButton(assets.get_skin("Menu"), "Play")
        play_button.addListener(ButtonListener(assets) {
            state.current_game = CurrentGame("0")
            state.set_screen(ScreenType.LOAD)
        })

        val table = Table()
        table.setFillParent(true)
        table.top()
        table.add(track_label).colspan(2).expandY()
        table.row()
        table.add(play_button).expandY()
        table.add(home_button).expandY()
        table.row()
        this.addActor(table)
    }

    override fun act(delta: Float) {
        super.act(delta)
    }

}