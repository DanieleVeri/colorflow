package com.colorflow.play.entity.dot

import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Path
import com.colorflow.utils.Color
import com.colorflow.utils.Position

/**
 * Created by daniele on 03/05/17.
 */

class DotPool private constructor() : Pool<Dot>() {

    override fun newObject(): Dot {
        return Dot()
    }

    operator fun get(type: Dot.Type, color: Color, pathType: Path.Type, start: Position.Radial, speed: Float): Dot {
        val d = obtain()
        d.set(type, color, pathType, start, speed)
        return d
    }

    companion object {
        val instance = DotPool()
    }
}
