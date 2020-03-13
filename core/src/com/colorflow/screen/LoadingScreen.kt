package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.colorflow.graphic.Position
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import com.colorflow.music.Music
import com.colorflow.state.Score
import kotlin.concurrent.thread
import kotlin.math.sin

class LoadingScreen(
        protected val state: GameState,
        protected val music: Music): Screen {

    private var t = -Math.PI / 2
    private val disclamer = Texture(Gdx.files.internal("skin/disclamer.png"))
    private val sprite = Sprite(disclamer)
    private val batch = SpriteBatch()

    override fun render(delta: Float) {
        t += Math.PI * delta
        Gdx.gl.glClearColor(
                (sin(t) / 2 + 0.5).toFloat()/4+0.5f,
                (sin(t) / 2 + 0.5).toFloat()/4+0.5f,
                (sin(t) / 2 + 0.5).toFloat()/4+0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        sprite.setPosition((Position.widthScreen - disclamer.width)/2, (Position.heightScreen - disclamer.height)/2)
        sprite.draw(batch)
        batch.end()
    }

    override fun show() {
        Gdx.input.inputProcessor = null
        if(state.current_game != null) {
            thread {
                Gdx.app.debug(this::class.java.simpleName, "loading & preparing track '${state.current_game!!.selected_track}'")
                music.prepare(state.current_game!!.selected_track)
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
