package com.colorflow

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.ads.IAdHandler
import com.colorflow.state.IStorage
import com.colorflow.screen.*
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import kotlin.concurrent.thread

class MainGame(
        protected val persistence: IStorage,
        protected val music_manager: IMusicManager,
        protected val music_analyzer: IMusicAnalyzer,
        protected val ad_handler: IAdHandler) : Game() {

    protected lateinit var assets: AssetProvider
    protected val game_state: GameState
    protected var disposed = false

    protected lateinit var load: Screen
    protected lateinit var menu: Screen
    protected lateinit var play: Screen
    protected lateinit var shop: Screen
    protected lateinit var track_selection: Screen
    protected lateinit var game_over: Screen

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
        if(disposed) return
        disposed = true

        menu.dispose()
        play.dispose()
        shop.dispose()
        assets.dispose()
        track_selection.dispose()
        game_over.dispose()

        super.dispose()
    }

    protected fun copy_internal_tracks() {
        if (!Gdx.files.local("music").exists())
            Gdx.files.internal("music").copyTo(Gdx.files.local("."))
    }

    protected fun set_screen_listener(screen: ScreenType) {
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