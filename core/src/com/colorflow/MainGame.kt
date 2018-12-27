package com.colorflow

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.colorflow.data.DataManager
import com.colorflow.music.MusicManagerInterface
import com.colorflow.screen.CreditsScreen
import com.colorflow.screen.LoadingScreen
import com.colorflow.screen.MenuScreen
import com.colorflow.screen.PlayScreen
import com.colorflow.screen.SettingsScreen
import com.colorflow.screen.ShopScreen
import com.colorflow.screen.SocialScreen
import com.colorflow.data.StorageInterface
import com.colorflow.utility.AssetProvider

class MainGame(storageInterface: StorageInterface, val musicManager: MusicManagerInterface) : Game() {

    var menu: Screen? = null
        private set
    var play: Screen? = null
        private set
    var credits: Screen? = null
        private set
    var settings: Screen? = null
        private set
    var social: Screen? = null
        private set
    var shop: Screen? = null
        private set
    val dataManager: DataManager = DataManager(storageInterface)
    var assetProvider: AssetProvider? = null
        private set

    override fun create() {
        Boot().start()
        setScreen(LoadingScreen())
    }

    override fun dispose() {
        super.dispose()
        if (menu != null) menu!!.dispose()
        if (play != null) play!!.dispose()
        if (credits != null) credits!!.dispose()
        if (settings != null) settings!!.dispose()
        if (social != null) social!!.dispose()
        if (shop != null) shop!!.dispose()
        if (assetProvider != null) assetProvider!!.dispose()
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
                credits = CreditsScreen(this@MainGame)
                settings = SettingsScreen(this@MainGame)
                social = SocialScreen(this@MainGame)
                shop = ShopScreen(this@MainGame)
                setScreen(menu)
            }
        }
    }

    private fun firstStart() {
        if (!Gdx.files.local("rings").exists()) {
            Gdx.files.internal("rings").copyTo(Gdx.files.local("rings"))
        }
        if (!Gdx.files.local("music").exists()) {
            Gdx.files.internal("music").copyTo(Gdx.files.local("music"))
        }
    }
}