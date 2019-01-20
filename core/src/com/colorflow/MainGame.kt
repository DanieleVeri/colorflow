package com.colorflow

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.colorflow.persistence.DataManager
import com.colorflow.play.IMusicManager
import com.colorflow.screen.LoadingScreen
import com.colorflow.screen.MenuScreen
import com.colorflow.screen.PlayScreen
import com.colorflow.screen.ShopScreen
import com.colorflow.persistence.StorageInterface
import com.colorflow.utility.AssetProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainGame(storage_interface: StorageInterface, val music_manager: IMusicManager) : Game() {

    var menu: Screen? = null
        private set
    var play: Screen? = null
        private set
    var shop: Screen? = null
        private set

    val persistence: DataManager = DataManager(storage_interface)
    lateinit var assets: AssetProvider
        private set

    override fun create() {
        setScreen(LoadingScreen())
        GlobalScope.launch {
            firstStart()
            assets = AssetProvider()
            music_manager.init()
            music_manager.analyze()
            Gdx.app.postRunnable {
                menu = MenuScreen(this@MainGame)
                play = PlayScreen(this@MainGame)
                shop = ShopScreen(this@MainGame)
                setScreen(menu)
            }
        }
    }

    override fun dispose() {
        super.dispose()
        menu?.dispose()
        play?.dispose()
        shop?.dispose()
        assets?.dispose()
    }


    private fun firstStart() {
        if (!Gdx.files.local("rings").exists()) {
            Gdx.files.internal("rings").copyTo(Gdx.files.local("."))
        }
        if (!Gdx.files.local("music").exists()) {
            Gdx.files.internal("music").copyTo(Gdx.files.local("."))
        }
    }
}