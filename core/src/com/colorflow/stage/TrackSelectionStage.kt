package com.colorflow.stage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.AssetProvider
import com.colorflow.ads.AdManager
import com.colorflow.state.CurrentGame
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.effects.EffectStage
import com.colorflow.graphic.Position
import com.colorflow.graphic.laction

class TrackSelectionStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad: AdManager): EffectStage(viewport) {

    init {
        val title = Label("Tracks", assets.get_skin("ui"), "h1")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })
        val coin_image = Image(assets.get_skin("ui").getRegion("coin"))
        val coin_label = Label("", assets.get_skin("ui"), "h3")
        coin_label.addAction(Actions.forever(laction { coin_label.setText(state.coins) }))
        val track_table = Table()
        val track_scroll = ScrollPane(track_table)

        val root_table = Table()
        root_table.width = Position.widthScreen
        root_table.height = Position.heightScreen
        root_table.top()
        root_table.add(home_button).left()
        root_table.add(title).expandX()
        root_table.row()
        root_table.add(track_scroll).expand().fill().colspan(2)
        root_table.row()
        root_table.add(coin_image)
        root_table.add(coin_label).left()
        addActor(root_table)

        state.track_list.forEach {
            val icon = Image(assets.get_skin("ui").getRegion("track"))
            val id_label = Label(it.id, assets.get_skin("ui"), "h3")
            id_label.setWrap(true)
            val play_button = ImageButton(assets.get_skin("ui"), "play")
            id_label.width = Position.widthScreen - icon.width *2 - play_button.width
            play_button.addListener(ButtonListener(assets, play_button) {
                state.current_game = CurrentGame(it.id)
                state.set_screen(ScreenType.LOAD)
            })
            val purchase_table = Table()
            val cost_label = Label(it.cost.toString(), assets.get_skin("ui"), "h2")
            val buy_button = ImageButton(assets.get_skin("ui"), "buy")
            buy_button.addListener(ButtonListener(assets, buy_button) {
                assets.get_sound("cash").play(1f)
                state.purchase_track(it)
                state.persist()
            })
            purchase_table.add(buy_button).expand()
            purchase_table.row()
            purchase_table.add(cost_label).expand()

            lateinit var cell: Cell<Table>
            track_table.addAction(Actions.forever(laction {
                cell.setActor(if(!it.purchased) purchase_table else play_button)
                buy_button.isDisabled = state.coins < it.cost || it.purchased
            }))

            track_table.add(icon)
            track_table.add(id_label).expandX().left().width(id_label.width).padLeft(15f)
            cell = track_table.add(play_button).padBottom(30f) as Cell<Table>
            track_table.row()
        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        effect_layer.explosion(Color.WHITE, Position.Pixel(screenX.toFloat(), Position.heightScreen - screenY.toFloat()))
        return super.touchDragged(screenX, screenY, pointer)
    }

}