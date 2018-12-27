package com.colorflow.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

//TODO: Issue on backButton pressed (invoke dispose)

public class AssetProvider implements Disposable {

    private AssetManager manager;
    private Map<String, String> skinMap, soundMap;

    public AssetProvider() {
        this.manager = new AssetManager();
        this.skinMap = new HashMap<String, String>();
        this.soundMap = new HashMap<String, String>();
        loadSounds();
        loadSkins();
    }

    private void loadSkins() {
        for (FileHandle file : Gdx.files.internal("skin/" + getResolution()).list()) {
            if (file.extension().equals("json")) {
                manager.load(file.pathWithoutExtension() + ".atlas", TextureAtlas.class);
                manager.load(file.path(), Skin.class, new SkinLoader.SkinParameter(file.pathWithoutExtension() + ".atlas"));
                skinMap.put(file.nameWithoutExtension(), file.path());
            }
        }
    }

    private void loadSounds() {
        for (FileHandle file : Gdx.files.internal("sounds").list()) {
            if (file.extension().equals("ogg")) {
                manager.load(file.path(), Sound.class);
                soundMap.put(file.nameWithoutExtension(), file.path());
            }
        }
    }

    public Skin getSkin(String skinName) {
        if (!manager.isLoaded(skinMap.get(skinName))) {
            manager.finishLoadingAsset(skinMap.get(skinName));
        }
        return manager.get(skinMap.get(skinName));
    }

    public Sound getSound(String soundName) {
        if (!manager.isLoaded(soundMap.get(soundName))) {
            manager.finishLoadingAsset(soundMap.get(soundName));
        }
        return manager.get(soundMap.get(soundName));
    }

    @Override
    public void dispose() {
        manager.dispose();
    }

    private String getResolution() {
        //TODO: Mock to  implement
        return "2560x1440";
    }

}