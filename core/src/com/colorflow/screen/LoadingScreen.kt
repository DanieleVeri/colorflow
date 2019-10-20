package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL30
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.music.Music
import com.colorflow.state.Score
import kotlin.concurrent.thread
import kotlin.math.sin

class LoadingScreen(
        protected val state: GameState,
        protected val music: Music): Screen {

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
        Gdx.input.inputProcessor = null
        if(state.current_game != null) {
            thread {
                Gdx.app.debug(this::class.java.simpleName, "reset music manager and load track '${state.current_game!!.selected_track}'")
                music.prepare(state.current_game!!.selected_track)
                Gdx.app.debug(this::class.java.simpleName, "music analyzer prepare track '${state.current_game!!.selected_track}'")
                music.analyze(state.current_game!!.selected_track)
                Gdx.app.postRunnable {
                    Gdx.app.debug(this::class.java.simpleName, "score reset")
                    state.current_game!!.score = Score()
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
