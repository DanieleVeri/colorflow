package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.colorflow.AssetProvider
import com.colorflow.GameState
import com.colorflow.os.IAdHandler
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
        Gdx.app.debug(this::class.java.simpleName, "earned: " + ad_handler.is_rewarded())
        super.resume()
    }
}