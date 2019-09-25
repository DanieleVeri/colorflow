package com.colorflow.screen

import com.colorflow.AssetProvider
import com.colorflow.GameState
import com.colorflow.stage.MenuStage

class MenuScreen(
        state: GameState,
        assets: AssetProvider) : UiScreen<MenuStage>(state, assets) {
    init {
        stage = MenuStage(viewport, state, assets)
        multiplexer.addProcessor(stage)
    }
}
