package com.colorflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.colorflow.database.SQLiteHelper
import com.colorflow.music.MusicManager

import java.util.ArrayList

class AndroidLauncher : AndroidApplication() {

    private var game: MainGame? = null
    private var sqliteDb: SQLiteHelper? = null
    private var musicManager: MusicManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askPermissions()
        val config = AndroidApplicationConfiguration()
        config.numSamples = 2
        config.useAccelerometer = false
        config.useCompass = false
        config.useImmersiveMode = true
        this.sqliteDb = SQLiteHelper(context)
        this.musicManager = MusicManager(context)
        this.game = MainGame(sqliteDb!!, musicManager!!)
        initialize(game, config)
    }

    override fun onDestroy() {
        super.onDestroy()
        sqliteDb!!.close()
        musicManager!!.release()
    }

    private fun askPermissions() {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val record_audio = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            if (record_audio != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO)
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toTypedArray(), 1)
            }
        }
    }
}
