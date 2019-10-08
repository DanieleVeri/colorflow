package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.AssetProvider
import com.colorflow.state.CurrentGame
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.ButtonListener

class TrackSelectionStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad_handler: IAdHandler): Stage(viewport) {

    private var coin_label: Label

    init {
        val title = Label("Tracks", assets.get_skin("ui"), "h1")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })
        coin_label = Label("", assets.get_skin("ui"), "h3")
        val track_table = Table()
        val track_scroll = ScrollPane(track_table)

        val table = Table()
        table.setFillParent(true)
        table.top()
        table.add(home_button).left()
        table.add(title).expandX()
        table.row()
        table.add(track_scroll).expand().fill().colspan(2)
        table.row()
        table.add(coin_label)
        addActor(table)

        state.track_list.forEach {
            val icon = Image(assets.get_skin("ui").getRegion("track"))
            val id_label = Label(it.id, assets.get_skin("ui"), "h3")
            val cost_label = Label(it.cost.toString(), assets.get_skin("ui"), "h2")
            val play_button = ImageButton(assets.get_skin("ui"), "play")
            play_button.addListener(ButtonListener(assets, play_button) {
                state.current_game = CurrentGame(it.id)
                state.set_screen(ScreenType.LOAD)
            })
            val buy_button = ImageButton(assets.get_skin("ui"), "buy")
            buy_button.addListener(ButtonListener(assets, play_button) {

            })
            track_table.add(icon)
            track_table.add(play_button)
            track_table.add(id_label).expandX()
            track_table.add(cost_label)
            track_table.add(buy_button)
            track_table.row()
        }
    }

    override fun act(delta: Float) {
        coin_label.setText("coins: ${state.coins}")
        super.act(delta)
    }

}