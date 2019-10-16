package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.colorflow.AssetProvider
import com.colorflow.ads.AdManager
import com.colorflow.state.GameState
import com.colorflow.graphic.UiScreen
import com.colorflow.stage.GameOverStage

class GameOverScreen(state: GameState,
                     assets: AssetProvider,
                     protected val ad: AdManager): UiScreen<GameOverStage>(state, assets) {
    init {
        stage = GameOverStage(viewport, state, assets, ad)
        multiplexer.addProcessor(stage)
    }

    override fun resume() {
        if(ad.is_rewarded()) {
            Gdx.app.debug(this::class.java.simpleName, "ad rewarded")
            ad.available = false
            stage.reward()
        }
        super.resume()
    }

    override fun show() {
        ad.available = true
        super.show()
    }

}