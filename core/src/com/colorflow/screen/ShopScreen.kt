package com.colorflow.screen

import com.colorflow.AssetProvider
import com.colorflow.GameState
import com.colorflow.os.IAdHandler
import com.colorflow.stage.ShopStage

class ShopScreen(
        state: GameState,
        assets: AssetProvider,
        ad_handler: IAdHandler) : UiScreen<ShopStage>(state, assets) {

    init {
        stage = ShopStage(viewport, state, assets, ad_handler)
        multiplexer.addProcessor(stage)
    }
}