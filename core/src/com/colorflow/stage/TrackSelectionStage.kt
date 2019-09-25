package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.AssetProvider
import com.colorflow.state.CurrentGame
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import com.colorflow.ads.IAdHandler
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.graphic.ButtonListener

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