package com.colorflow.screen

import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.ads.IAdHandler
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.stage.TrackSelectionStage

class TrackSelectionScreen(state: GameState,
                           assets: AssetProvider,
                           ad_handler: IAdHandler,
                           protected val music_manager: IMusicManager,
                           protected val music_analyzer: IMusicAnalyzer): UiScreen<TrackSelectionStage>(state, assets) {
    init {
        stage = TrackSelectionStage(viewport, state, assets, ad_handler, music_manager, music_analyzer)
        multiplexer.addProcessor(stage)
    }
}