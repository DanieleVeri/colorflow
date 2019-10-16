package com.colorflow.graphic.effects.particle

import com.badlogic.gdx.utils.Pool
import com.colorflow.AssetProvider

class ParticlePool(protected val assets: AssetProvider): Pool<Particle>() {

    override fun newObject(): Particle {
        return Particle(assets, this)
    }

    fun get(): Particle {
        return obtain()
    }

}

