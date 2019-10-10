package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.engine.ring.Ring
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.action

class MenuStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider): Stage(viewport) {

    private var play_button: Button
    private var shop_button: Button
    private var dy = 0.0

    init {
        val title = Label("COLORFLOW", assets.get_skin("ui"), "h1")
        val record = Label("REC0RD: " + state.record, assets.get_skin("ui"), "h2")
        record.addAction(Actions.forever(action { record.setText("REC0RD: " + state.record) }))
        val coins = Label("COINS: " + state.coins, assets.get_skin("ui"), "h2")
        coins.addAction(Actions.forever(action{ coins.setText("COINS: " + state.coins) }))
        play_button = ImageButton(assets.get_skin("ui"), "menu_play")
        shop_button = ImageButton(assets.get_skin("ui"), "shop")
        play_button.addListener(ButtonListener(assets, play_button) {state.set_screen(ScreenType.TRACK_SELECTION)})
        shop_button.addListener(ButtonListener(assets, shop_button) {state.set_screen(ScreenType.SHOP)})
        val ring = Ring(assets, state.ring_list.find { it.used }!!.src)
        ring.addAction(Actions.forever(Actions.rotateBy(1f)))

        val table = Table()
        table.setFillParent(true)
        table.top()
        table.add(title).colspan(2).expandY()
        table.row()
        table.add(ring).colspan(2).expand()
        table.row()
        table.add(play_button).expandY()
        table.add(record).expandY()
        table.row()
        table.add(shop_button).expandY()
        table.add(coins).expandY()
        addActor(table)
    }

    override fun act(delta: Float) {
        play_button.moveBy(0f, Math.sin(dy).toFloat() / 2)
        shop_button.moveBy(0f, Math.sin(1+dy).toFloat() / 2)
        dy += delta.toDouble()
        super.act(delta)
    }
}