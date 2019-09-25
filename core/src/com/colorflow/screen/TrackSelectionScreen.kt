package com.colorflow.screen

import com.colorflow.AssetProvider
import com.colorflow.GameState
import com.colorflow.os.IAdHandler
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
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