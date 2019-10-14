package com.colorflow.graphic.effects.explosion

import com.badlogic.gdx.utils.Pool
import com.colorflow.graphic.effects.explosion.Explosion

class ExplosionPool: Pool<Explosion>() {

    override fun newObject(): Explosion {
        return Explosion(this)
    }

    fun get(): Explosion {
        return obtain()
    }

}

