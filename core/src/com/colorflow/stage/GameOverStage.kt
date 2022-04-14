package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
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

class GameOverStage (
        viewport: Viewport,
        protected val state: GameState,
        protected val assets: AssetProvider,
        protected val ad: AdManager): EffectStage(viewport) {

    protected val ad_button: ImageButton

    init {
        val score = Label("", assets.get_skin("ui"), "h3")
        score.addAction(Actions.forever(laction {
            score.setText("SCORE: " + state.current_game!!.score.points + "\nBEST: " + state.record)
        }))
        val coins = Label("", assets.get_skin("ui"), "h3")
        score.addAction(Actions.forever(laction {
            coins.setText("COINS: " + state.current_game!!.score.coins)
        }))
        val title = Label("GAME OVER", assets.get_skin("ui"), "h2")
        val ads_text = Label("x2 coins ->", assets.get_skin("ui"), "h3")

        val restart_button = ImageButton(assets.get_skin("ui"), "restart")
        ad_button = ImageButton(assets.get_skin("ui"), "ads")
        val home_button = ImageButton(assets.get_skin("ui"), "back")

        restart_button.addListener(ButtonListener(assets, restart_button) {
            state.current_game = CurrentGame(state.current_game!!.selected_track)
            state.set_screen(ScreenType.LOAD)
        })
        ad_button.addListener(ButtonListener(assets, ad_button) {
            ad.show()
        })
        home_button.addListener(ButtonListener(assets, home_button) {
            state.current_game = null
            state.set_screen(ScreenType.MENU)
        })
        val table = Table()
        table.width = Position.widthScreen
        table.height = Position.heightScreen
        table.add(title).colspan(2).expandX()
        table.row()
        table.add(score).expandX().left()
        table.add(coins).expandX().right()
        table.row()
        table.add(ads_text)
        table.add(ad_button).colspan(2).expand()
        table.row()
        table.add(restart_button).expand()
        table.add(home_button).expand()
        addActor(table)

        addAction(Actions.forever(laction {
            ad_button.isDisabled = !ad.available
        }))
    }

    fun reward() {
        assets.get_sound("cash").play(1f)
        state.coins += state.current_game!!.score.coins
        state.persist()
        state.current_game!!.score.coins *= 2
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        effect_layer.explosion(Color.WHITE, Position.Pixel(screenX.toFloat(), Position.heightScreen - screenY.toFloat()))
        return super.touchDragged(screenX, screenY, pointer)
    }

}