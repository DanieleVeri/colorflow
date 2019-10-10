package com.colorflow.stage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.action

class ShopStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad_handler: IAdHandler): Stage(viewport) {

    init {
        val title = Label("Upgrade", assets.get_skin("ui"), "h1")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })
        val coin_image = Image(assets.get_skin("ui").getRegion("coin"))
        val coin_label = Label("", assets.get_skin("ui"), "h3")
        coin_label.addAction(Actions.forever(action { coin_label.setText("${state.coins}") }))

        val ring_table = Table()
        val ring_scroll = ScrollPane(ring_table)
        val bonus_table = Table()
        val bonus_scroll = ScrollPane(bonus_table)

        val root_table = Table()
        root_table.setFillParent(true)
        root_table.top()
        root_table.add(home_button).left()
        root_table.add(title).expandX()
        root_table.row()
        root_table.add(ring_scroll).expand().fill().colspan(2)
        root_table.row()
        root_table.add(bonus_scroll).expand().fill().colspan(2)
        root_table.row()
        root_table.add(coin_image)
        root_table.add(coin_label).left()
        addActor(root_table)

        state.ring_list.forEach {
            val icon = Image(assets.get_skin("game").getRegion(it.src))
            val id_label = Label(it.id, assets.get_skin("ui"), "h3")
            val purchase_table = Table()
            val cost_label = Label(it.cost.toString(), assets.get_skin("ui"), "h2")
            val buy_button = ImageButton(assets.get_skin("ui"), "buy")
            buy_button.addListener(ButtonListener(assets, buy_button) {
                assets.get_sound("cash").play(1f)
                state.purchase_ring(it)
                state.persist()
            })
            val select_button = ImageButton(assets.get_skin("ui"), "play")
            select_button.addListener(ButtonListener(assets, select_button) {
                state.select_ring(it.id)
                state.persist()
            })
            purchase_table.add(buy_button).expand()
            purchase_table.row()
            purchase_table.add(cost_label).expand()

            lateinit var cell: Cell<Table>
            ring_table.addAction(Actions.forever(action {
                if(it.used)
                    icon.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f))
                else
                    icon.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f))
                select_button.isVisible = !it.used
                cell.setActor(if(!it.purchased) purchase_table else select_button)
                buy_button.isDisabled = state.coins < it.cost || it.purchased
            }))

            ring_table.add(icon).padRight(30f)
            ring_table.add(id_label).expandX().left().width(id_label.width).padLeft(15f)
            cell = ring_table.add(purchase_table).padBottom(30f)
            ring_table.row()
        }

        /* Bonus BOMB */
        val bomb_table = Table()
        val bomb_label = Label("+5% chance", assets.get_skin("ui"), "h3")
        val drawable_bomb = TextureRegionDrawable(assets.get_skin("game").getRegion("bonus_bomb"))
        val bomb_button = ImageButton(drawable_bomb)
        bomb_button.addListener(ButtonListener(assets, bomb_button) {
            assets.get_sound("cash").play(1f)
            // TODO
        })
        bomb_table.add(bomb_button)
        bomb_table.row()
        bomb_table.add(bomb_label)
        /* Bonus COIN */
        val coin_table = Table()
        val coin_bonus_label = Label("+5% chance", assets.get_skin("ui"), "h3")
        val drawable_coin = TextureRegionDrawable(assets.get_skin("game").getRegion("dot_coin"))
        val coin_bonus_button = ImageButton(drawable_coin)
        coin_bonus_button.addListener(ButtonListener(assets, coin_bonus_button) {
            assets.get_sound("cash").play(1f)
            // TODO
        })
        coin_table.add(coin_bonus_button)
        coin_table.row()
        coin_table.add(coin_bonus_label)

        bonus_table.add(bomb_table).expandX().padLeft(40f)
        bonus_table.add(coin_table).expandX()
    }

}