package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL30
import com.colorflow.GameState
import com.colorflow.ScreenType
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
import kotlin.concurrent.thread
import kotlin.math.sin

class LoadingScreen(
        protected val state: GameState,
        protected val music_manager: IMusicManager,
        protected val music_analyzer: IMusicAnalyzer): Screen {

    private var t = -Math.PI / 2

    override fun render(delta: Float) {
        t += Math.PI * delta
        Gdx.gl.glClearColor(
                (sin(t) / 2 + 0.5).toFloat(),
                (sin(t) / 2 + 0.5).toFloat(),
                (sin(t) / 2 + 0.5).toFloat(), 1f)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
    }

    override fun show() {
        if(state.current_game != null) {
            thread {
                Gdx.app.debug(this::class.java.simpleName, "reset music manager and load track '${state.current_game!!.selected_track}'")
                music_manager.reset()
                music_manager.load(state.current_game!!.selected_track)
                Gdx.app.debug(this::class.java.simpleName, "music analyzer prepare track '${state.current_game!!.selected_track}'")
                music_analyzer.analyze_beat(state.current_game!!.selected_track)
                music_analyzer.prepare(state.current_game!!.selected_track)
                val paused = state.current_game!!.paused
                Gdx.app.postRunnable {
                    Gdx.app.debug(this::class.java.simpleName, "score reset")
                    state.current_game!!.score.reset()
                    if(paused)
                        state.current_game!!.paused = true
                    state.set_screen(ScreenType.PLAY)
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {}
}
