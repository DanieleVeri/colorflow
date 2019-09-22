package com.colorflow

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.colorflow.os.IMusicAnalyzer
import com.colorflow.os.IMusicManager
import com.colorflow.os.IAdHandler
import com.colorflow.screen.LoadingScreen
import com.colorflow.screen.MenuScreen
import com.colorflow.screen.PlayScreen
import com.colorflow.screen.ShopScreen
import com.colorflow.os.IStorage
import com.colorflow.utils.AssetProvider
import kotlin.concurrent.thread

class MainGame(
        private val storage: IStorage,
        private val music_manager: IMusicManager,
        private val music_analyzer: IMusicAnalyzer,
        private val ad_handler: IAdHandler) : Game() {

    private lateinit var _assets: AssetProvider

    private lateinit var load: Screen
    private lateinit var menu: Screen
    private lateinit var play: Screen
    private lateinit var shop: Screen

    private var _disposed = false

    override fun create() {
        Gdx.app.logLevel =  LOG_DEBUG
        load = LoadingScreen()
        ScreenManager.add_cb(::_set_screen_listener)
        ScreenManager.set(ScreenType.LOAD)
        thread {
            Gdx.app.debug("LoaderThread", "start loading assets")
            _first_start()
            _assets = AssetProvider()
            Gdx.app.postRunnable {
                _assets.finish_loading()
                Gdx.app.debug("LoaderThread", "assets ready")
                menu = MenuScreen(storage, _assets)
                play = PlayScreen(storage, _assets, music_manager, music_analyzer, ad_handler)
                shop = ShopScreen(storage, _assets)
                ScreenManager.set(ScreenType.MENU)
            }
        }
    }

    override fun dispose() {
        if(_disposed) return
        _disposed = true
        super.dispose()
        ScreenManager.rem_cb(::_set_screen_listener)
        menu.dispose()
        play.dispose()
        shop.dispose()
        _assets.dispose()
    }

    private fun _first_start() {
        if (!Gdx.files.local("music").exists())
            Gdx.files.internal("music").copyTo(Gdx.files.local("."))
    }

    private fun _set_screen_listener(screen: ScreenType) {
        when (screen) {
            ScreenType.LOAD -> setScreen(load)
            ScreenType.MENU -> setScreen(menu)
            ScreenType.PLAY -> setScreen(play)
            ScreenType.SHOP -> setScreen(shop)
        }
    }
}