package com.colorflow.music

import android.content.Context
import java.io.File

fun get_music_file(context: Context, track_id: String)
    = File(context.applicationInfo.dataDir + "/files/music/" + track_id + ".mp3")
