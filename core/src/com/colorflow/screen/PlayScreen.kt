package com.colorflow.screen

import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.stage.HUDStage
import com.colorflow.stage.PlayStage
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType

class PlayScreen(
        game_state: GameState,
        assets: AssetProvider,
        private val music_manager: IMusicManager,
        private val music_analyzer: IMusicAnalyzer) : UiScreen<PlayStage>(game_state, assets) {

    private val play_stage: PlayStage
    private val hud_stage: HUDStage

    private var prev_paused = false

    init {
        play_stage = PlayStage(viewport, game_state, assets)
        hud_stage = HUDStage(viewport, game_state, assets)
        music_analyzer.add_listener(play_stage)
    }

    override fun render(delta: Float) {
        if(state.current_game!!.gameover) {
            game_over()
            return
        }
        if(state.current_game!!.paused && !prev_paused) {
            game_pause()
        }
        if(!state.current_game!!.paused && (prev_paused || !state.current_game!!.started)) {
            game_play()
        }

        if(!state.current_game!!.paused)
            play_stage.act(delta)
        hud_stage.act(delta)
        play_stage.draw()
        hud_stage.draw()

        prev_paused = state.current_game!!.paused
    }

    protected fun game_over() {
        music_manager.stop()
        music_analyzer.pause_time()
        state.set_screen(ScreenType.GAME_OVER)
    }

    protected fun game_pause() {
        if(state.current_game!!.started) {
            music_manager.pause()
            music_analyzer.pause_time()
        }
        multiplexer.clear()
        multiplexer.addProcessor(hud_stage)
    }

    protected fun game_play() {
        if(!state.current_game!!.started) {
            state.current_game!!.started = true
            assets.get_sound("start").play(1f)
        }
        music_manager.play()
        music_analyzer.play_time()
        multiplexer.clear()
        multiplexer.addProcessor(play_stage)
        multiplexer.addProcessor(play_stage.get_ring_listener())
        multiplexer.addProcessor(hud_stage)
    }

    override fun pause() {
        game_pause()
    }

    override fun show() {
        play_stage.reset()
        super.show()
    }

    override fun resume() {
        state.current_game!!.paused = true
    }

    override fun dispose() {
        play_stage.dispose()
        hud_stage.dispose()
    }
}