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

class MainGame(storageInterface: StorageInterface, val musicManager: IMusicManager) : Game() {

    var menu: Screen? = null
        private set
    var play: Screen? = null
        private set
    var shop: Screen? = null
        private set

    val dataManager: DataManager = DataManager(storageInterface)
    lateinit var assetProvider: AssetProvider
        private set

    override fun create() {
        Boot().start()
        setScreen(LoadingScreen())
    }

    override fun dispose() {
        super.dispose()
        menu?.dispose()
        play?.dispose()
        shop?.dispose()
        assetProvider?.dispose()
    }

    internal inner class Boot : Thread() {
        override fun run() {
            firstStart()
            musicManager.init()
            assetProvider = AssetProvider()
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
            }

            Gdx.app.postRunnable {
                menu = MenuScreen(this@MainGame)
                play = PlayScreen(this@MainGame)
                shop = ShopScreen(this@MainGame)
                setScreen(menu)
            }
        }
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