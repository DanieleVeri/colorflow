package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
import com.colorflow.os.IAdHandler
import com.colorflow.stage.HUDStage
import com.colorflow.stage.PlayStage
import com.colorflow.AssetProvider
import com.colorflow.GameState
import com.colorflow.ScreenType

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
        music_analyzer.add_beat_cb(play_stage::on_beat)
    }

    override fun render(delta: Float) {
        // Game over
        if(state.current_game!!.gameover) {
            Gdx.app.debug(this::class.java.simpleName, "state -- game over")
            music_manager.stop()
            music_analyzer.pause_time()
            state.set_screen(ScreenType.GAME_OVER)
            return
        }

        // Paused
        if(state.current_game!!.paused && !prev_paused) {
            Gdx.app.debug(this::class.java.simpleName, "state -- paused")
            if(state.current_game!!.started) {
                music_manager.pause()
                music_analyzer.pause_time()
            }
            multiplexer.clear()
            multiplexer.addProcessor(hud_stage)
        }

        // Play
        if(!state.current_game!!.paused && (prev_paused || !state.current_game!!.started)) {
            // Start
            if(!state.current_game!!.started) {
                Gdx.app.debug(this::class.java.simpleName, "state -- started")
                state.current_game!!.started = true
                play_stage.reset()
                assets.get_sound("start").play(1f)
            }
            Gdx.app.debug(this::class.java.simpleName, "state -- play")
            music_manager.play()
            music_analyzer.play_time()
            multiplexer.clear()
            multiplexer.addProcessor(play_stage)
            multiplexer.addProcessor(play_stage.get_ring_listener())
            multiplexer.addProcessor(hud_stage)
        }

        if(state.current_game!!.paused) {
            hud_stage.act(delta)
            play_stage.draw()
            hud_stage.draw()
        } else {
            play_stage.act(delta)
            hud_stage.act(delta)
            play_stage.draw()
            hud_stage.draw()
        }

        prev_paused = state.current_game!!.paused
    }

    override fun pause() {
        Gdx.app.debug(this::class.java.simpleName, "state -- paused")
        if(state.current_game!!.started) {
            music_manager.pause()
            music_analyzer.pause_time()
        }
        multiplexer.clear()
        multiplexer.addProcessor(hud_stage)
    }

    override fun resume() {
        state.current_game!!.paused = true
    }

    override fun dispose() {
        play_stage.dispose()
        hud_stage.dispose()
    }
}