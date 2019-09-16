package com.colorflow.play.entity

import com.colorflow.utils.Position

class Path(var type: Type, var pos: Position.Radial, var velocity: Float) {

    enum class Type {
        RADIAL, SPIRAL
    }

    fun nextPos(dt: Float): Position {
        when (type) {
            Path.Type.RADIAL -> pos.set_dist(pos.distRadial - velocity)
            Path.Type.SPIRAL -> {
                pos.set_angle(pos.angleRadial - velocity / 2)
                pos.set_dist(pos.distRadial - velocity)
            }
        }
        return pos
    }
}
