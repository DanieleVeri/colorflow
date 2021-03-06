package com.colorflow.screen

import com.colorflow.AssetProvider
import com.colorflow.ads.AdManager
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.UiScreen
import com.colorflow.stage.ShopStage

class ShopScreen(
        state: GameState,
        assets: AssetProvider,
        ad: AdManager) : UiScreen<ShopStage>(state, assets) {

    init {
        stage = ShopStage(viewport, state, assets, ad)
        multiplexer.addProcessor(stage)
    }
}