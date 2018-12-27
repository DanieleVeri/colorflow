package com.colorflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.colorflow.database.SQLiteHelper;
import com.colorflow.music.MusicManager;

import java.util.ArrayList;
import java.util.List;

public class AndroidLauncher extends AndroidApplication {

    private MainGame game;
    private SQLiteHelper sqliteDb;
    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermissions();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.numSamples = 2;
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useImmersiveMode = true;
        this.sqliteDb = new SQLiteHelper(getContext());
        this.musicManager = new MusicManager(getContext());
        this.game = new MainGame(sqliteDb, musicManager);
        initialize(game, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqliteDb.close();
        musicManager.release();
    }

    private void askPermissions() {
        List<String> permissions = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int RECORD_AUDIO = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            }
        }
    }
}
