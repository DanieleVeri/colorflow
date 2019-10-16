package com.colorflow

import com.badlogic.gdx.*
import com.badlogic.gdx.Application.LOG_DEBUG
import com.colorflow.ads.AdManager
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.ads.IAdHandler
import com.colorflow.graphic.effects.EffectLayer
import com.colorflow.music.Music
import com.colorflow.state.IStorage
import com.colorflow.screen.*
import com.colorflow.state.GameState
import com.colorflow.state.ScreenType
import kotlin.concurrent.thread

class MainGame(
        persistence: IStorage,
        music_manager: IMusicManager,
        music_analyzer: IMusicAnalyzer,
        protected val ad_handler: IAdHandler) : Game() {

    protected lateinit var assets: AssetProvider
    protected val game_state: GameState
    protected val music: Music
    protected val ad_manager: AdManager
    protected var disposed = false

    protected lateinit var load: Screen
    protected lateinit var menu: Screen
    protected lateinit var play: Screen
    protected lateinit var shop: Screen
    protected lateinit var track_selection: Screen
    protected lateinit var game_over: Screen

    init {
        game_state = GameState(persistence, this::set_screen_listener)
        music = Music(music_analyzer, music_manager)
        ad_manager = AdManager(ad_handler)
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG

        load = LoadingScreen(game_state, music)
        game_state.set_screen(ScreenType.LOAD)

        thread {
            Gdx.app.debug("LoaderThread", "loading game state")
            game_state.load()
            Gdx.app.debug("LoaderThread", "game state loaded")

            if (!Gdx.files.local("music").exists()) {
                Gdx.app.debug("LoaderThread", "copying internal music")
                Gdx.files.internal("music").copyTo(Gdx.files.local("."))
            }

            Gdx.app.debug("LoaderThread", "loading assets")
            assets = AssetProvider()
            Gdx.app.postRunnable {
                assets.finish_loading()
                EffectLayer.init(assets)
                Gdx.app.debug("LoaderThread", "assets ready")

                menu = MenuScreen(game_state, assets)
                play = PlayScreen(game_state, assets, music)
                shop = ShopScreen(game_state, assets, ad_manager)
                game_over = GameOverScreen(game_state, assets, ad_manager)
                track_selection = TrackSelectionScreen(game_state, assets, ad_manager)

                game_state.set_screen(ScreenType.MENU)
            }
        }
    }

    override fun dispose() {
        if(disposed)
            return
        disposed = true

        menu.dispose()
        play.dispose()
        shop.dispose()
        track_selection.dispose()
        game_over.dispose()

        music.dispose()
        EffectLayer.manager.dispose()
        assets.dispose()

        super.dispose()
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