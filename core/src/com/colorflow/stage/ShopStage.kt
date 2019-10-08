package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.ButtonListener

class ShopStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad_handler: IAdHandler): Stage(viewport) {

    private var coin_label: Label

    init {
        val title = Label("Upgrade", assets.get_skin("ui"), "h1")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })
        coin_label = Label("", assets.get_skin("ui"), "h3")

        val table = Table()
        table.setFillParent(true)
        table.top()
        table.add(home_button).left()
        table.add(title).expandX()
        table.row()
        table.add(coin_label).colspan(2)
        addActor(table)
    }

    override fun act(delta: Float) {
        coin_label.setText("coins: ${state.coins}")
        super.act(delta)
    }

}