package com.colorflow

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
import com.colorflow.os.IAdHandler
import com.colorflow.os.IStorage
import com.colorflow.screen.*
import kotlin.concurrent.thread

class MainGame(
        private val persistence: IStorage,
        private val music_manager: IMusicManager,
        private val music_analyzer: IMusicAnalyzer,
        private val ad_handler: IAdHandler) : Game() {

    private lateinit var assets: AssetProvider

    private lateinit var load: Screen
    private lateinit var menu: Screen
    private lateinit var play: Screen
    private lateinit var shop: Screen
    private lateinit var track_selection: Screen
    private lateinit var game_over: Screen

    private val game_state: GameState

    private var _disposed = false

    init {
        game_state = GameState(persistence, this::set_screen_listener)
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG

        load = LoadingScreen(game_state, music_manager, music_analyzer)
        game_state.set_screen(ScreenType.LOAD)

        thread {
            Gdx.app.debug("LoaderThread", "start loading game state")
            game_state.load()
            Gdx.app.debug("LoaderThread", "game state loaded")

            Gdx.app.debug("LoaderThread", "start loading assets")
            copy_internal_tracks()
            assets = AssetProvider()
            Gdx.app.postRunnable {
                assets.finish_loading()
                Gdx.app.debug("LoaderThread", "assets ready")

                menu = MenuScreen(game_state, assets)
                play = PlayScreen(game_state, assets, music_manager, music_analyzer)
                shop = ShopScreen(game_state, assets, ad_handler)
                game_over = GameOverScreen(game_state, assets, ad_handler)
                track_selection = TrackSelectionScreen(game_state, assets, ad_handler, music_manager, music_analyzer)

                game_state.set_screen(ScreenType.MENU)
            }
        }
    }

    override fun dispose() {
        if(_disposed) return
        _disposed = true

        menu.dispose()
        play.dispose()
        shop.dispose()
        assets.dispose()
        track_selection.dispose()
        game_over.dispose()

        super.dispose()
    }

    private fun copy_internal_tracks() {
        if (!Gdx.files.local("music").exists())
            Gdx.files.internal("music").copyTo(Gdx.files.local("."))
    }

    private fun set_screen_listener(screen: ScreenType) {
        when (screen) {
            ScreenType.LOAD -> setScreen(load)
            ScreenType.MENU -> setScreen(menu)
            ScreenType.PLAY -> setScreen(play)
            ScreenType.SHOP -> setScreen(shop)
            ScreenType.TRACK_SELECTION -> setScreen(track_selection)
            ScreenType.GAME_OVER -> setScreen(game_over)
        }
    }
}