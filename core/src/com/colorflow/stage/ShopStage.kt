package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.ScreenManager
import com.colorflow.ScreenType
import com.colorflow.utils.AssetProvider
import com.colorflow.os.IStorage
import com.colorflow.play.ring.Ring
import com.colorflow.utils.ButtonListener

class ShopStage (
        viewport: Viewport,
        private val persistence: IStorage,
        private val assets: AssetProvider): Stage(viewport) {

    private var coins = 0
    private var table: Table = Table()
    private var homeBtn: ImageButton? = null
    private var coinLabel: Label = Label("", assets.get_skin("Shop"), "Coins")
    private var ringScroll: ScrollPane? = null
    private var ringList: Table = Table()

    init {
        table.setFillParent(true)
        table.pad(30f)
        homeBtn = ImageButton(assets.get_skin("Shop"), "Home")
        homeBtn!!.addListener(ButtonListener(assets, on_tap = {ScreenManager.set(ScreenType.MENU)}))
        ringScroll = ScrollPane(ringList)
        val title = Label("Have a lucky day", assets.get_skin("Shop"), "Title")
        table.add<ImageButton>(homeBtn).left()
        table.add<Label>(title)
        table.row()
        table.add<ScrollPane>(ringScroll).expand().fill().colspan(2)
        table.row()
        table.add<Label>(coinLabel).colspan(2)
        this.addActor(table)

        persistence.rings.map {
            val ring = Ring(assets, it.src)
            ring.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(360f, 7f)))
            val name = Label(it.src, assets.get_skin("Shop"), "ItemName")
            val cost = TextButton(it.cost.toString(), assets.get_skin("Shop"), "Buy")
            cost.addListener(ButtonListener(assets, on_tap = {
                persistence.transaction {
                    persistence.purchase_ring(it.id)
                    persistence.coins -= it.cost
                }
                coins -= it.cost
                cost.isDisabled = true
                cost.touchable = Touchable.disabled
            }))
            if(it.purchased) {
                cost.isDisabled = true
                cost.touchable = Touchable.disabled
            }
            ringList.add(ring).expandX()
            ringList.add(name).expandX()
            ringList.add(cost).expandX()
            ringList.row().pad(20f)
        }
    }

    override fun act(delta: Float) {
        coinLabel.setText("coins: $coins")

        ringList.cells.filter { it is TextButton }.map { it as TextButton
            it.isDisabled = it.isDisabled || it.text.toString().toInt() > coins
            it.touchable = if(it.isDisabled) Touchable.disabled else Touchable.enabled
        }
        super.act(delta)
    }

    fun sync_from_storage() {
        coins = persistence.coins
    }
}