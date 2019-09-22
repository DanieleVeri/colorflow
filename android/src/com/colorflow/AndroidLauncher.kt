package com.colorflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.colorflow.database.SQLiteManager
import com.colorflow.music.MusicAnalyzer
import com.colorflow.music.MusicManager

import java.util.ArrayList

class AndroidLauncher : AndroidApplication() {
    private lateinit var game: MainGame
    private lateinit var sqlite: SQLiteManager
    private lateinit var music_manager: MusicManager
    private lateinit var music_analyzer: MusicAnalyzer
    private lateinit var ad_handler: AdHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.numSamples = 2
        config.useAccelerometer = false
        config.useCompass = false
        config.useImmersiveMode = true
        askPermissions()

        sqlite = SQLiteManager(context)
        music_manager = MusicManager(context)
        music_analyzer = MusicAnalyzer(context)
        ad_handler = AdHandler(this)

        game = MainGame(sqlite, music_manager, music_analyzer, ad_handler)
        initialize(game, config)
    }

    override fun onDestroy() {
        super.onDestroy()
        game.dispose()
        sqlite.close()
        music_manager.release()
    }

    private fun askPermissions() {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val record_audio = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            if (record_audio != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.RECORD_AUDIO)

            if (!permissions.isEmpty())
                requestPermissions(permissions.toTypedArray(), 1)
        }
    }
}
