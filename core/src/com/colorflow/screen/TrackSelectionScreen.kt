package com.colorflow.screen

import com.colorflow.AssetProvider
import com.colorflow.ads.AdManager
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.UiScreen
import com.colorflow.stage.TrackSelectionStage

class TrackSelectionScreen(state: GameState,
                           assets: AssetProvider,
                           ad: AdManager): UiScreen<TrackSelectionStage>(state, assets) {
    init {
        stage = TrackSelectionStage(viewport, state, assets, ad)
        multiplexer.addProcessor(stage)
    }
}