package com.colorflow.graphic.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.Disposable
import com.colorflow.AssetProvider
import com.colorflow.graphic.effects.particle.Particle
import com.colorflow.graphic.effects.particle.ParticlePool
import com.colorflow.graphic.effects.shader.ShaderEffect

internal class EffectManager (assets: AssetProvider): Disposable {
    val fbo: FrameBuffer
    protected val effect_list: ArrayList<ShaderEffect>
    protected val particle_pool: ParticlePool

    init {
        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        effect_list = ArrayList()
        particle_pool = ParticlePool(assets)

        effect_list.add(ShaderEffect(assets.get_shader("twinkling"), "twinkling"))
        effect_list.add(ShaderEffect(assets.get_shader("shockwave"), "shockwave"))
        effect_list.add(ShaderEffect(assets.get_shader("glow"), "glow"))
    }

    fun get_effect(name: String): ShaderEffect {
        return effect_list.find { it.name == name }!!
    }

    fun apply_effects(batch: Batch, texture: Texture): Texture {
        var current = texture
        effect_list.forEach {
            current = it.apply(batch, current)
        }
        return current
    }

    fun update_effects(delta: Float) {
        effect_list.forEach { it.update(delta) }
    }

    fun stop_all_effects() {
        effect_list.forEach { it.stop() }
    }

    fun get_explosion_particle(): Particle {
        return particle_pool.get()
    }

    override fun dispose() {
        effect_list.forEach { it.dispose() }
        particle_pool.clear()
        fbo.dispose()
    }

}