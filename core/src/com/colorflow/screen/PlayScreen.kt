package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.stage.HUDStage
import com.colorflow.stage.PlayStage
import com.colorflow.play.Score
import com.colorflow.persistence.AssetProvider
import com.colorflow.persistence.IStorage
import com.colorflow.utils.Position
import com.colorflow.utils.effects.ShockWave

class PlayScreen(
                 private val persistence: IStorage,
                 assets: AssetProvider,
                 private val music_manager: IMusicManager,
                 private val music_analyzer: IMusicAnalyzer) : Screen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val cameraFlipY: OrthographicCamera
    private val multiplexer: InputMultiplexer

    private val score: Score
    private val playStage: PlayStage
    private val hudStage: HUDStage

    enum class State { PLAY, PAUSE, OVER }
    var state: State = State.PLAY
        set(state) {
            when (state) {
                PlayScreen.State.PLAY -> {
                    music_manager.play()
                    music_analyzer.play_time()

                    multiplexer.clear()
                    multiplexer.addProcessor(playStage)
                    multiplexer.addProcessor(playStage.get_ring_listener())
                    multiplexer.addProcessor(hudStage)
                }
                PlayScreen.State.PAUSE -> {
                    music_manager.pause()
                    music_analyzer.pause_time()

                    multiplexer.clear()
                    multiplexer.addProcessor(hudStage)
                }
                PlayScreen.State.OVER -> {
                    music_manager.stop()
                    music_analyzer.pause_time()

                    persistence.transaction {
                        persistence.coins += score.coins
                    }
                    if (persistence.record < score.points)
                        persistence.record = score.points
                    multiplexer.clear()
                    multiplexer.addProcessor(hudStage)
                }
            }
            hudStage.setState(state)
            field = state
        }

    init {
        this.camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        this.camera.update()
        this.cameraFlipY = OrthographicCamera()
        this.cameraFlipY.setToOrtho(true, Position.widthScreen, Position.heightScreen)
        this.cameraFlipY.update()
        this.multiplexer = InputMultiplexer()

        this.score = Score()
        this.playStage = PlayStage(ScreenViewport(this.cameraFlipY), persistence, score, this)
        this.hudStage = HUDStage(ScreenViewport(this.camera), assets, score, this)

        music_analyzer.add_beat_cb(playStage::on_beat)

    }

    override fun show() {
        reset()
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        playStage.act(delta)
        hudStage.act(delta)
        playStage.draw()
        hudStage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {
        if (this.state == State.PLAY)
            state = State.PAUSE
    }

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        playStage.dispose()
        hudStage.dispose()
    }

    fun reset() {
        music_manager.reset()
        music_analyzer.prepare("0")
        score.reset()
        score.record = persistence.record
        playStage.reset()
        state = State.PLAY
    }
}