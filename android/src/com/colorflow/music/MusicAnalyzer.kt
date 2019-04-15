package com.colorflow.music

import android.content.Context
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

class MusicAnalyzer(private val context: Context) {
    external fun detect(path: String): Array<BeatSample>
    private lateinit var _detect_task: Deferred<Unit>

    private val _tempos: HashMap<String, Array<BeatSample>> = HashMap()

    fun analyze(file_name: String) {
        val file = File(context.applicationInfo.dataDir + "/files/music/" + file_name + ".wav")
        _detect_task = GlobalScope.async {
            _tempos[file_name] = detect(file.absolutePath)
        }
    }
}