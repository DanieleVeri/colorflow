package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.ScreenManager
import com.colorflow.ScreenType
import com.colorflow.utils.AssetProvider
import com.colorflow.persistence.IStorage
import com.colorflow.play.ring.Ring
import com.colorflow.utils.ButtonListener

class MenuStage (
        viewport: Viewport,
        private val persistence: IStorage,
        assets: AssetProvider): Stage(viewport) {

    private var coins = 0
    private var record = 0
    private var recordLabel: Label
    private var coinsLabel: Label
    private var play_button: Button
    private var slot_button: Button
    private var ring: Ring
    private var dy = 0.0

    init {
        val title = Label("COLORFLOW", assets.get_skin("Menu"), "Title")
        recordLabel = Label("REC0RD: " + persistence.record, assets.get_skin("Menu"), "Menu")
        coinsLabel = Label("COINS: " + persistence.coins, assets.get_skin("Menu"), "Menu")
        play_button = ImageButton(assets.get_skin("Menu"), "Play")
        slot_button = ImageButton(assets.get_skin("Menu"), "Slot")
        play_button.addListener(ButtonListener(assets, on_tap = { ScreenManager.set(ScreenType.PLAY) }))
        slot_button.addListener(ButtonListener(assets, on_tap = { ScreenManager.set(ScreenType.SHOP)}))
        ring = Ring(assets, persistence.used_ring.src)

        val table = Table()
        table.setFillParent(true)
        table.top()
        table.add(title).colspan(2).expandY()
        table.row()
        table.add(ring).colspan(2).expand()
        table.row()
        table.add(play_button).expandY()
        table.add<Label>(recordLabel).expandY()
        table.row()
        table.add(slot_button).expandY()
        table.add<Label>(coinsLabel).expandY()
        this.addActor(table)
    }

    override fun act(delta: Float) {
        recordLabel.setText("REC0RD: " + record)
        coinsLabel.setText("COINS: " + coins)
        ring.rotateBy(1f)
        play_button.moveBy(0f, Math.sin(dy).toFloat() / 2)
        slot_button.moveBy(0f, Math.sin(1+dy).toFloat() / 2)
        dy += delta.toDouble()
        super.act(delta)
    }

    fun sync_from_storage() {
        coins = persistence.coins
        record = persistence.record
    }
}