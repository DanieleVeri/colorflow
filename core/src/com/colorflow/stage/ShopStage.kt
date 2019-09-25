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

    private var coins = 0
    private var table: Table
    private var homeBtn: ImageButton
    private var coin_label: Label

    init {
        table = Table()
        table.setFillParent(true)
        table.pad(30f)
        homeBtn = ImageButton(assets.get_skin("Shop"), "Home")
        homeBtn.addListener(ButtonListener(assets, on_tap = {state.set_screen(ScreenType.MENU)}))
        coin_label = Label("", assets.get_skin("Shop"), "Coins")
        val title = Label("Have a lucky day", assets.get_skin("Shop"), "Title")
        table.add<ImageButton>(homeBtn).left()
        table.add<Label>(title)
        table.row()
        table.add<Label>(coin_label).colspan(2)
        addActor(table)
    }

    override fun act(delta: Float) {
        coin_label.setText("coins: $coins")
        super.act(delta)
    }

}