package com.colorflow.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable

import java.util.HashMap

class AssetProvider : Disposable {

    private val _manager: AssetManager = AssetManager()
    private val _skin_map: MutableMap<String, String>
    private val _sound_map: MutableMap<String, String>
    private val _texture_map: MutableMap<String, String>
    private val _resolution: String
        // get() = Position.heightScreen.toInt().toString()+"x"+ Position.widthScreen.toInt().toString()
        get() = "2560x1440"

    init {
        this._skin_map = HashMap()
        this._sound_map = HashMap()
        this._texture_map = HashMap()
        _load_sounds()
        _load_skins()
        _load_textures()
    }

    fun finish_loading() {
        _manager.finishLoading()
    }

    private fun _load_skins() {
        val files = Gdx.files.internal("skin/$_resolution").list()
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

    private fun _load_textures() {
        val files = Gdx.files.internal("skin/$_resolution/textures").list()
        files.filter { it.extension() == "png" }.forEach { file ->
            Gdx.app.debug("asset-provider", "load texture "+file.path() + file.nameWithoutExtension())
            _manager.load<Texture>(file.path(), Texture::class.java)
            _texture_map[file.nameWithoutExtension()] = file.path()
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

    fun get_texture(texture_name: String): Texture {
        if (!_manager.isLoaded(_texture_map[texture_name])) {
            _manager.finishLoadingAsset(_texture_map[texture_name])
        }
        return _manager.get<Texture>(_texture_map[texture_name])
    }

    override fun dispose() {
        _manager.dispose()
    }

}