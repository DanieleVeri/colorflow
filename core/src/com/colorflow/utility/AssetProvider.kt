package com.colorflow.utility

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable

import java.util.HashMap

class AssetProvider : Disposable {

    private val manager: AssetManager = AssetManager()
    private val skinMap: MutableMap<String, String>
    private val soundMap: MutableMap<String, String>

    private val resolution: String
        get() = Position.heightScreen.toInt().toString()+"x"+Position.widthScreen.toInt().toString()

    init {
        this.skinMap = HashMap()
        this.soundMap = HashMap()
        loadSounds()
        loadSkins()
    }

    private fun loadSkins() {
        for (file in Gdx.files.internal("skin/$resolution").list()) {
            if (file.extension() == "json") {
                manager.load(file.pathWithoutExtension() + ".atlas", TextureAtlas::class.java)
                manager.load(file.path(), Skin::class.java, SkinLoader.SkinParameter(file.pathWithoutExtension() + ".atlas"))
                skinMap[file.nameWithoutExtension()] = file.path()
            }
        }
    }

    private fun loadSounds() {
        for (file in Gdx.files.internal("sounds").list()) {
            if (file.extension() == "ogg") {
                manager.load(file.path(), Sound::class.java)
                soundMap[file.nameWithoutExtension()] = file.path()
            }
        }
    }

    fun getSkin(skinName: String): Skin {
        if (!manager.isLoaded(skinMap[skinName])) {
            manager.finishLoadingAsset(skinMap[skinName])
        }
        return manager.get(skinMap[skinName])
    }

    fun getSound(soundName: String): Sound {
        if (!manager.isLoaded(soundMap[soundName])) {
            manager.finishLoadingAsset(soundMap[soundName])
        }
        return manager.get(soundMap[soundName])
    }

    override fun dispose() {
        manager.dispose()
    }

}