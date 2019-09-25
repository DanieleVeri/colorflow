package com.colorflow.play.entity.bonus

import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Path
import com.colorflow.AssetProvider
import com.colorflow.utils.Position

class BonusPool(private val assets: AssetProvider) : Pool<Bonus>() {

    override fun newObject(): Bonus {
        return Bonus(assets, this)
    }

    fun get(type: Bonus.Type, pathType: Path.Type, start: Position.Radial, speed: Float): Bonus {
        val b = obtain()
        b.set(type, pathType, start, speed)
        return b
    }
}
