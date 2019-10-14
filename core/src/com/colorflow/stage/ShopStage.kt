package com.colorflow.stage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.state.ScreenType
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.ButtonListener
import com.colorflow.graphic.effects.EffectStage
import com.colorflow.graphic.Position
import com.colorflow.graphic.laction

class ShopStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad_handler: IAdHandler): EffectStage(viewport) {

    init {
        val title = Label("Upgrade", assets.get_skin("ui"), "h1")
        val home_button = ImageButton(assets.get_skin("ui"), "back")
        home_button.addListener(ButtonListener(assets, home_button) {
            state.set_screen(ScreenType.MENU)
        })
        val coin_image = Image(assets.get_skin("ui").getRegion("coin"))
        val coin_label = Label("", assets.get_skin("ui"), "h3")
        coin_label.addAction(Actions.forever(laction { coin_label.setText("${state.coins}") }))
        val ring_table = Table()
        val ring_scroll = ScrollPane(ring_table)
        val bonus_table = Table()
        val bonus_scroll = ScrollPane(bonus_table)
        val root_table = Table()
        root_table.width = Position.widthScreen
        root_table.height = Position.heightScreen
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
            ring_table.add(icon).padRight(30f)
            ring_table.add(id_label).expandX().left().width(id_label.width).padLeft(15f)
            cell = ring_table.add(purchase_table).padBottom(30f)
            ring_table.row()

            addAction(Actions.forever(laction {
                if(it.used)
                    icon.addAction(Actions.scaleTo(1.2f, 1.2f, 0.2f))
                else
                    icon.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f))
                select_button.isVisible = !it.used
                cell.setActor(if(!it.purchased) purchase_table else select_button)
                buy_button.isDisabled = state.coins < it.cost || it.purchased
            }))
        }

        /* Bonus BOMB */
        val bomb_table = Table()
        val bomb_label = Label("", assets.get_skin("ui"), "h3")
        val bomb_button = ImageButton(TextureRegionDrawable(assets.get_skin("game").getRegion("bonus_bomb")))
        bomb_button.addListener(ButtonListener(assets, bomb_button) {
            assets.get_sound("cash").play(1f)
            state.coins -= (state.bomb_chance * BONUS_COST).toInt()
            state.bomb_chance += BONUS_CHANCE_INC
            state.persist()
        })
        bomb_table.add(bomb_button)
        bomb_table.row()
        bomb_table.add(bomb_label)
        /* Bonus GOLD */
        val gold_table = Table()
        val gold_label = Label("", assets.get_skin("ui"), "h3")
        val gold_bonus_button = ImageButton(TextureRegionDrawable(assets.get_skin("game").getRegion("dot_coin")))
        gold_bonus_button.addListener(ButtonListener(assets, gold_bonus_button) {
            assets.get_sound("cash").play(1f)
            state.coins -= (state.gold_chance * BONUS_COST).toInt()
            state.gold_chance += BONUS_CHANCE_INC
            state.persist()
        })
        gold_table.add(gold_bonus_button)
        gold_table.row()
        gold_table.add(gold_label)

        bonus_table.add(bomb_table).expandX().padLeft(40f)
        bonus_table.add(gold_table).expandX()
        addAction(Actions.forever(laction {
            val bomb_cost = (state.bomb_chance * BONUS_COST).toInt()
            val gold_cost = (state.gold_chance * BONUS_COST).toInt()
            bomb_label.setText("$bomb_cost -> +5%")
            gold_label.setText("$gold_cost -> +5%")
            bomb_button.isDisabled = state.coins < bomb_cost
            gold_bonus_button.isDisabled = state.coins < gold_cost
        }))
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        effect_layer.explosion(Color.WHITE, Position.Pixel(screenX.toFloat(), Position.heightScreen - screenY.toFloat()))
        return super.touchDragged(screenX, screenY, pointer)
    }

    companion object {
        const val BONUS_COST = 10000
        const val BONUS_CHANCE_INC = 0.05f
    }

}