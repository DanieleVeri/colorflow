package com.colorflow.play.entity

import com.badlogic.gdx.utils.Pool
import com.colorflow.utils.Position

/**
 * Created by daniele on 09/05/17.
 */

class Path(type: Type, pos: Position.Radial, speed: Float) : Pool.Poolable {

    private var type: Type? = null
    val pos: Position.Radial = Position.Radial(1f, 1f)
    private var speed: Float = 0.toFloat()

    enum class Type {
        RADIAL, SPIRAL
    }

    init {
        set(type, pos, speed)
    }

    operator fun set(type: Type, pos: Position.Radial, speed: Float) {
        this.type = type
        this.pos.x = pos.x
        this.pos.y = pos.y
        this.speed = speed
    }

    fun nextPos(dt: Float): Position {
        when (type) {
            Path.Type.RADIAL -> pos.set_dist(pos.distRadial - speed)
            Path.Type.SPIRAL -> {
                pos.set_angle(pos.angleRadial - speed / 2)
                pos.set_dist(pos.distRadial - speed)
            }
            else -> throw IllegalStateException()
        }
        return pos
    }

    override fun reset() {

    }
}
