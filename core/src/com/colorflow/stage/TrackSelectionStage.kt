package com.colorflow.stage

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
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
        protected val ad_handler: IAdHandler): Stage(viewport) {

    init {
        val title = Label("Tracks", assets.get_skin("ui"), "h1")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })

        val track_table = Table()
        val track_scroll = ScrollPane(track_table)

        val table = Table()
        table.setFillParent(true)
        table.top()
        table.add(title).expandX()
        table.add(home_button)
        table.row()
        table.add(track_scroll).expand().fill().right()
        table.row()
        addActor(table)

        state.track_list.forEach {
            val icon = assets.get_skin("ui").getRegion("track")
            val track_label = Label(it.id, assets.get_skin("ui"), "h2")
            val play_button = ImageButton(assets.get_skin("ui"), "play")
            play_button.addListener(ButtonListener(assets, play_button) {
                state.current_game = CurrentGame(it.id)
                state.set_screen(ScreenType.LOAD)
            })
            val buy_button = ImageButton(assets.get_skin("ui"), "buy")
            buy_button.addListener(ButtonListener(assets, play_button) {

            })
            track_table.add(play_button)
            track_table.add(track_label).expandX()
            track_table.add(buy_button)
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
    }

}