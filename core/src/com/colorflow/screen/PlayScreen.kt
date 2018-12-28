package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.MainGame
import com.colorflow.music.BeatDetector
import com.colorflow.music.IBeatDetector
import com.colorflow.play.HUDStage
import com.colorflow.play.PlayStage
import com.colorflow.play.Score
import com.colorflow.utility.Position

class PlayScreen(val game: MainGame) : Screen {
    private val camera: OrthographicCamera = OrthographicCamera()
    private val cameraFlipY: OrthographicCamera
    val score: Score
    private val playStage: PlayStage
    private val hudStage: HUDStage
    private val beatDetector: IBeatDetector
    var state: State? = null
        set(state) {
            when (state) {
                PlayScreen.State.PLAY -> {
                    game.musicManager.play()
                    multiplexer.clear()
                    multiplexer.addProcessor(playStage)
                    multiplexer.addProcessor(playStage.ring!!.getListener())
                    multiplexer.addProcessor(hudStage)
                }
                PlayScreen.State.PAUSE -> {
                    game.musicManager.pause()
                    multiplexer.clear()
                    multiplexer.addProcessor(hudStage)
                }
                PlayScreen.State.OVER -> {
                    game.musicManager.stop()
                    game.dataManager.incCoins(score.coins)
                    if (game.dataManager.record < score.points) {
                        game.dataManager.record = score.points
                    }
                    multiplexer.clear()
                    multiplexer.addProcessor(hudStage)
                }
                else -> throw IllegalStateException()
            }
            playStage.setState(state)
            hudStage.setState(state)
            field = state
        }
    private val multiplexer: InputMultiplexer

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.cameraFlipY = OrthographicCamera()
        this.cameraFlipY.setToOrtho(true, Position.widthScreen, Position.heightScreen)
        this.cameraFlipY.update()
        this.playStage = PlayStage(ScreenViewport(this.cameraFlipY), this)
        this.hudStage = HUDStage(ScreenViewport(this.camera), this)
        this.score = Score()
        this.score.addObserver(hudStage)
        this.beatDetector = BeatDetector(game.musicManager)
        this.multiplexer = InputMultiplexer()
    }

    override fun show() {
        reset()
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        playStage.act(delta)
        hudStage.act(delta)
        playStage.draw()
        hudStage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {
        if (this.state == State.PLAY) {
            state = State.PAUSE
        }
    }

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        playStage.dispose()
        hudStage.dispose()
    }

    fun reset() {
        game.musicManager.reset()
        score.reset()
        playStage.reset()
        state = State.PLAY
    }

    enum class State {
        PLAY, PAUSE, OVER
    }

}