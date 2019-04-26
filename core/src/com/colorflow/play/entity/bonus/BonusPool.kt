package com.colorflow.play.entity.bonus

import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Path
import com.colorflow.utils.Position

/**
 * Created by daniele on 08/05/17.
 */

class BonusPool private constructor() : Pool<Bonus>() {

    override fun newObject(): Bonus {
        return Bonus()
    }

    operator fun get(type: Bonus.Type, pathType: Path.Type, start: Position.Radial, speed: Float): Bonus {
        val b = obtain()
        b.set(type, pathType, start, speed)
        return b
    }

    companion object {
        val instance = BonusPool()
    }
}
