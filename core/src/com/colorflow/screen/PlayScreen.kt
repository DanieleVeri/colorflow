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
import com.colorflow.utils.AssetProvider
import com.colorflow.persistence.IStorage
import com.colorflow.utils.Position

class PlayScreen(
        private val persistence: IStorage,
        assets: AssetProvider,
        private val music_manager: IMusicManager,
        private val music_analyzer: IMusicAnalyzer) : Screen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val cameraFlipY: OrthographicCamera
    private val multiplexer: InputMultiplexer
    private val score: Score
    private val _play_stage: PlayStage
    private val _hud_stage: HUDStage

    enum class State { PLAY, PAUSE, OVER }
    var state: State = State.PLAY
        set(state) {
            when (state) {
                PlayScreen.State.PLAY -> {
                    music_manager.play()
                    music_analyzer.play_time()
                    multiplexer.clear()
                    multiplexer.addProcessor(_play_stage)
                    multiplexer.addProcessor(_play_stage.get_ring_listener())
                    multiplexer.addProcessor(_hud_stage)
                }
                PlayScreen.State.PAUSE -> {
                    music_manager.pause()
                    music_analyzer.pause_time()
                    multiplexer.clear()
                    multiplexer.addProcessor(_hud_stage)
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
                    multiplexer.addProcessor(_hud_stage)
                }
            }
            _hud_stage.setState(state)
            field = state
        }

    init {
        camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        camera.update()
        cameraFlipY = OrthographicCamera()
        cameraFlipY.setToOrtho(true, Position.widthScreen, Position.heightScreen)
        cameraFlipY.update()
        multiplexer = InputMultiplexer()
        score = Score()
        _play_stage = PlayStage(ScreenViewport(this.cameraFlipY), assets, persistence, score, this)
        _hud_stage = HUDStage(ScreenViewport(this.camera), assets, score, this)
        music_analyzer.add_beat_cb(_play_stage::on_beat)
    }

    override fun show() {
        reset()
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        _play_stage.act(delta)
        _hud_stage.act(delta)
        _play_stage.draw()
        _hud_stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {
        if (this.state == State.PLAY)
            state = State.PAUSE
    }

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        _play_stage.dispose()
        _hud_stage.dispose()
    }

    fun reset() {
        Gdx.app.debug("PlayScreen", "reset music manager and load track '0'")
        music_manager.reset()
        music_manager.load("0")
        Gdx.app.debug("PlayScreen", "music analyzer prepare track '0'")
        music_analyzer.analyze_beat("0")
        music_analyzer.prepare("0")
        Gdx.app.debug("PlayScreen", "score reset and fetch record")
        score.reset()
        score.record = persistence.record
        Gdx.app.debug("PlayScreen", "play_stage reset")
        _play_stage.reset()
        state = State.PLAY
    }
}