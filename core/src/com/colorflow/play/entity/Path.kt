package com.colorflow.play.entity

import com.colorflow.graphic.Position

class Path(var type: Type,
           var pos: Position.Radial,
           var static_velocity: Float) {
    var get_dyn_velocity: ()->Float = {0f}

    enum class Type {
        RADIAL_STATIC, RADIAL_DYNAMIC, SPIRAL
    }

    fun nextPos(dt: Float): Position {
        when (type) {
            Type.RADIAL_STATIC -> pos.set_dist(pos.distRadial - static_velocity)
            Type.RADIAL_DYNAMIC -> pos.set_dist(pos.distRadial - get_dyn_velocity())
            Type.SPIRAL -> {
                pos.set_angle(pos.angleRadial - static_velocity / 2)
                pos.set_dist(pos.distRadial - static_velocity)
            }
        }
        return pos
    }
}
