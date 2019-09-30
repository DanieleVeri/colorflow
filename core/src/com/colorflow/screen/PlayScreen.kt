package com.colorflow.screen

import com.colorflow.stage.HUDStage
import com.colorflow.stage.PlayStage
import com.colorflow.AssetProvider
import com.colorflow.music.BeatSample
import com.colorflow.music.IEventListener
import com.colorflow.music.Music
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType

class PlayScreen(
        game_state: GameState,
        assets: AssetProvider,
        private val music: Music) : UiScreen<PlayStage>(game_state, assets), IEventListener {

    private val play_stage: PlayStage
    private val hud_stage: HUDStage

    private var prev_paused = false

    init {
        play_stage = PlayStage(viewport, game_state, assets)
        hud_stage = HUDStage(viewport, game_state, assets)
        music.add_listener(play_stage)
        music.add_listener(this)
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
        music.stop()
        assets.get_sound("backspin").play(1f)
        state.set_screen(ScreenType.GAME_OVER)
    }

    override fun on_completition() {
        assets.get_sound("complete").play(1f)
        state.set_screen(ScreenType.GAME_OVER)
    }

    protected fun game_pause() {
        if(state.current_game!!.started)
            music.pause()
        multiplexer.clear()
        multiplexer.addProcessor(hud_stage)
    }

    protected fun game_play() {
        if(!state.current_game!!.started) {
            state.current_game!!.started = true
            assets.get_sound("start").play(1f)
        }
        music.play()
        multiplexer.clear()
        multiplexer.addProcessor(play_stage)
        multiplexer.addProcessor(play_stage.get_ring_listener())
        multiplexer.addProcessor(hud_stage)
    }

    override fun pause() {
        game_pause()
    }

    override fun show() {
        play_stage.update()
        super.show()
    }

    override fun resume() {
        state.current_game!!.paused = true
    }

    override fun dispose() {
        play_stage.dispose()
        hud_stage.dispose()
    }

    override suspend fun on_beat(sample: BeatSample) {}
}