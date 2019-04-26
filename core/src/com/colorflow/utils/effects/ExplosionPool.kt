package com.colorflow.utils.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Pool
import com.colorflow.utils.Position

/**
 * Created by daniele on 09/05/17.
 */

class ExplosionPool private constructor() : Pool<Explosion>() {

    override fun newObject(): Explosion {
        return Explosion()
    }

    fun start(stage: Stage, color: Color, position: Position) {
        val e = obtain()
        stage.addActor(e)
        e.start(color, position)
    }

    companion object {
        val instance = ExplosionPool()
    }

}

