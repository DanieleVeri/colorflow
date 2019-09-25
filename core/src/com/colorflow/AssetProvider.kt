package com.colorflow

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable

import java.util.HashMap
import kotlin.math.abs

class AssetProvider : Disposable {

    private val _manager: AssetManager = AssetManager()
    private val _skin_map: MutableMap<String, String>
    private val _sound_map: MutableMap<String, String>
    private val _screen_density: String

    init {
        this._skin_map = HashMap()
        this._sound_map = HashMap()

        val dpi = (Gdx.graphics.density * 160f).toInt()
        var best_match = Densisties.hdpi
        Densisties.values().forEach {
            if(abs(dpi - it.dpi()) < abs(dpi - best_match.dpi()))
                best_match = it
        }
        _screen_density = best_match.name
        Gdx.app.debug(this::class.java.simpleName, "screen density detected: " + _screen_density)

        _load_sounds()
        _load_skins()
    }

    fun finish_loading() {
        _manager.finishLoading()
    }

    private fun _load_skins() {
        val files = Gdx.files.internal("skin/$_screen_density").list()
        files.filter { it.extension() == "json" }.forEach { file ->
            Gdx.app.debug("asset-provider", "load skin "+file.path())
            _manager.load(file.pathWithoutExtension() + ".atlas", TextureAtlas::class.java)
            _manager.load(file.path(), Skin::class.java, SkinLoader.SkinParameter(file.pathWithoutExtension() + ".atlas"))
            _skin_map[file.nameWithoutExtension()] = file.path()
        }
    }

    private fun _load_sounds() {
        val files = Gdx.files.internal("sounds").list()
        files.filter { it.extension() == "ogg" }.forEach { file ->
            Gdx.app.debug("asset-provider", "load sound "+file.path())
            _manager.load(file.path(), Sound::class.java)
            _sound_map[file.nameWithoutExtension()] = file.path()
        }
    }

    fun get_skin(skin_name: String): Skin {
        if (!_manager.isLoaded(_skin_map[skin_name])) {
            _manager.finishLoadingAsset(_skin_map[skin_name])
        }
        return _manager.get(_skin_map[skin_name])
    }

    fun get_sound(soundName: String): Sound {
        if (!_manager.isLoaded(_sound_map[soundName])) {
            _manager.finishLoadingAsset(_sound_map[soundName])
        }
        return _manager.get(_sound_map[soundName])
    }

    override fun dispose() {
        _manager.dispose()
    }

    enum class Densisties {
        hdpi { override fun dpi() = 240},
        xhdpi { override fun dpi() = 320},
        xxhdpi { override fun dpi() = 480},
        xxxhdpi { override fun dpi() = 640};

        abstract fun dpi(): Int
    }

}