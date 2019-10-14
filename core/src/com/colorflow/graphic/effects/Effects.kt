package com.colorflow.graphic.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.Disposable
import com.colorflow.graphic.effects.explosion.ExplosionPool
import com.colorflow.graphic.effects.shader.ShaderEffect

object Effects: Disposable {
    var fbo: FrameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    val explosion_pool: ExplosionPool
    val shader_effects: ArrayList<ShaderEffect> = ArrayList()

    init {
        shader_effects.add(ShaderEffect("shockwave"))
        shader_effects.add(ShaderEffect("twinkling"))
        shader_effects.add(ShaderEffect("glow"))
        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        explosion_pool = ExplosionPool()
    }

    override fun dispose() {
        fbo.dispose()
        shader_effects.forEach { it.dispose() }
        explosion_pool.clear()
    }
}