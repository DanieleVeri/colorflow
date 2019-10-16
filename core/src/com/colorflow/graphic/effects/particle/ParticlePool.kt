package com.colorflow.graphic.effects.particle

import com.badlogic.gdx.utils.Pool

class ParticlePool: Pool<Particle>() {

    override fun newObject(): Particle {
        return Particle(this)
    }

    fun get(): Particle {
        return obtain()
    }

}

