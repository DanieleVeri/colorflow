package com.colorflow.utils.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Pool
import com.colorflow.utils.Position

class ExplosionPool: Pool<Explosion>() {

    override fun newObject(): Explosion {
        return Explosion(this)
    }

    fun start(stage: Stage, color: Color, position: Position) {
        val e = obtain()
        stage.addActor(e)
        e.start(color, position)
    }

}

