package com.colorflow

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.screen.LoadingScreen
import com.colorflow.screen.MenuScreen
import com.colorflow.screen.PlayScreen
import com.colorflow.screen.ShopScreen
import com.colorflow.persistence.IStorage
import com.colorflow.persistence.AssetProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainGame(
        private val storage: IStorage,
        private val music_manager: IMusicManager,
        private val music_analyzer: IMusicAnalyzer) : Game() {

    private var _assets: AssetProvider? = null

    private var load: Screen? = null
    private var menu: Screen? = null
    private var play: Screen? = null
    private var shop: Screen? = null

    override fun create() {
        load = LoadingScreen()
        ScreenManager.add_cb(::_set_screen_listener)
        ScreenManager.set(ScreenType.LOAD)
        GlobalScope.launch {
            _first_start()
            _assets = AssetProvider()
            music_manager.load("0")
            music_analyzer.analyze_beat("0")
            music_analyzer.prepare("0")
            Gdx.app.postRunnable {
                menu = MenuScreen(this@MainGame, storage, _assets!!)
                play = PlayScreen(storage, _assets!!, music_manager, music_analyzer)
                shop = ShopScreen(storage, _assets!!)
                ScreenManager.set(ScreenType.MENU)
            }
        }
    }

    override fun dispose() {
        super.dispose()
        ScreenManager.rem_cb(::_set_screen_listener)
        menu?.dispose()
        play?.dispose()
        shop?.dispose()
        _assets?.dispose()
    }

    private fun _first_start() {
        if (!Gdx.files.local("rings").exists())
            Gdx.files.internal("rings").copyTo(Gdx.files.local("."))

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