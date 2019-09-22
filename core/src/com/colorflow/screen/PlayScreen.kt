package com.colorflow.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
import com.colorflow.os.IAdHandler
import com.colorflow.stage.HUDStage
import com.colorflow.stage.PlayStage
import com.colorflow.play.Score
import com.colorflow.utils.AssetProvider
import com.colorflow.os.IStorage
import com.colorflow.utils.Position
import kotlin.concurrent.thread

class PlayScreen(
        private val persistence: IStorage,
        private val assets: AssetProvider,
        private val music_manager: IMusicManager,
        private val music_analyzer: IMusicAnalyzer,
        private val ad_handler: IAdHandler) : Screen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val cameraFlipY: OrthographicCamera
    private val multiplexer: InputMultiplexer
    private val score: Score
    private val _play_stage: PlayStage
    private val _hud_stage: HUDStage

    init {
        camera.setToOrtho(false, Position.widthScreen, Position.heightScreen)
        camera.update()
        cameraFlipY = OrthographicCamera()
        cameraFlipY.setToOrtho(true, Position.widthScreen, Position.heightScreen)
        cameraFlipY.update()
        multiplexer = InputMultiplexer()
        score = Score()
        _play_stage = PlayStage(ScreenViewport(this.cameraFlipY), assets, persistence, score, this)
        _hud_stage = HUDStage(ScreenViewport(this.camera), assets, score, this, ad_handler)
        music_analyzer.add_beat_cb(_play_stage::on_beat)
    }

    override fun show() {
        reset()
        Gdx.input.inputProcessor = multiplexer
    }

    fun reset() {
        state = State.LOADING
        thread {
            Gdx.app.debug("PlayScreen", "reset music manager and load track '0'")
            music_manager.reset()
            music_manager.load("0")
            Gdx.app.debug("PlayScreen", "music analyzer prepare track '0'")
            music_analyzer.analyze_beat("0")
            music_analyzer.prepare("0")
            val paused = _paused
            Gdx.app.postRunnable {
                _started = false
                Gdx.app.debug("PlayScreen", "score reset and fetch record")
                score.reset()
                score.record = persistence.record
                Gdx.app.debug("PlayScreen", "play_stage reset")
                _play_stage.reset()
                state = State.PLAY
                if(paused)
                    state = State.PAUSE
            }
        }

    }

    private var _started = false
    enum class State { PLAY, PAUSE, OVER, LOADING }
    var state: State = State.LOADING
        set(state) {
            when (state) {
                State.PLAY -> {
                    if(_started.not()) {
                        assets.get_sound("start").play(1f)
                        _started = true
                    }
                    music_manager.play()
                    music_analyzer.play_time()
                    multiplexer.clear()
                    multiplexer.addProcessor(_play_stage)
                    multiplexer.addProcessor(_play_stage.get_ring_listener())
                    multiplexer.addProcessor(_hud_stage)
                }
                State.PAUSE -> {
                    music_manager.pause()
                    music_analyzer.pause_time()
                    multiplexer.clear()
                    multiplexer.addProcessor(_hud_stage)
                }
                State.OVER -> {
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
                State.LOADING -> {
                    multiplexer.clear()
                }
            }
            _hud_stage.setState(state)
            field = state
        }

    private var t = 0.0
    override fun render(delta: Float) {
        if(state == State.LOADING) {
            t += Math.PI * delta
            Gdx.gl.glClearColor((Math.sin(t) / 2 + 0.5).toFloat(),
                    (Math.sin(t) / 2 + 0.5).toFloat(),
                    (Math.sin(t) / 2 + 0.5).toFloat(), 1f)
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
            return
        }
        _play_stage.act(delta)
        _hud_stage.act(delta)
        _play_stage.draw()
        _hud_stage.draw()
    }

    override fun resize(width: Int, height: Int) {}

    private var _paused = false
    override fun pause() {
        _paused = true
        if (this.state == State.PLAY)
            state = State.PAUSE
    }

    override fun resume() {
        _paused = false
        Gdx.app.debug(this::class.java.simpleName, "earned: " + ad_handler.is_rewarded())
    }

    override fun hide() {}

    override fun dispose() {
        _play_stage.dispose()
        _hud_stage.dispose()
    }
}