package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.stage.GameOverStage

class GameOverScreen(state: GameState,
                     assets: AssetProvider,
                     protected val ad_handler: IAdHandler): UiScreen<GameOverStage>(state, assets) {
    init {
        stage = GameOverStage(viewport, state, assets, ad_handler)
        multiplexer.addProcessor(stage)
    }

    override fun show() {
        stage.update()
        super.show()
    }

    override fun resume() {
        if(ad_handler.is_rewarded()) {
            Gdx.app.debug(this::class.java.simpleName, "rewarded")
            stage.reward()
        }
        super.resume()
    }
}