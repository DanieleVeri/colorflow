package com.colorflow.graphic.effects

import com.badlogic.gdx.utils.Pool

class ExplosionPool: Pool<Explosion>() {

    override fun newObject(): Explosion {
        return Explosion(this)
    }

    fun get(): Explosion {
        return obtain()
    }

}

