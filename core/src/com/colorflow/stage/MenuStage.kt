package com.colorflow.stage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.Position
import com.colorflow.graphic.effects.EffectStage
import com.colorflow.graphic.laction

class MenuStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider): EffectStage(viewport) {

    private var play_button: Button
    private var shop_button: Button
    private var dy = 0.0

    init {
        effect_layer.spectrum {}

        val title = Label("COLORFLOW", assets.get_skin("ui"), "h1")
        val record = Label("REC0RD: " + state.record, assets.get_skin("ui"), "h2")
        record.addAction(Actions.forever(laction { record.setText("REC0RD: " + state.record) }))
        val coins = Label("COINS: " + state.coins, assets.get_skin("ui"), "h2")
        coins.addAction(Actions.forever(laction{ coins.setText("COINS: " + state.coins) }))
        play_button = ImageButton(assets.get_skin("ui"), "menu_play")
        shop_button = ImageButton(assets.get_skin("ui"), "shop")
        play_button.addListener(ButtonListener(assets, play_button) {
            state.set_screen(ScreenType.TRACK_SELECTION)
        })
        shop_button.addListener(ButtonListener(assets, shop_button) {
            state.set_screen(ScreenType.SHOP)
        })

        val table = Table()
        table.width = Position.widthScreen
        table.height = Position.heightScreen
        table.top()
        table.add(title).colspan(2).expandY()
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

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        effect_layer.explosion(Color.WHITE, Position.Pixel(screenX.toFloat(), Position.heightScreen - screenY.toFloat()))
        return super.touchDragged(screenX, screenY, pointer)
    }

}