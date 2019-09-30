package com.colorflow.play.entity.bonus

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Path
import com.colorflow.AssetProvider
import com.colorflow.graphic.Position

class BonusPool(protected val assets: AssetProvider) : Pool<Bonus>() {
    protected val used_bonus: ArrayList<Bonus> = ArrayList()

    override fun newObject(): Bonus {
        return Bonus(assets, this)
    }

    fun get(type: Bonus.Type, pathType: Path.Type, start: Position.Radial, speed: Float): Bonus {
        val b = obtain()
        b.set(type, pathType, start, speed)
        used_bonus.add(b)
        return b
    }

    override fun free(`object`: Bonus?) {
        used_bonus.remove(`object`)
        super.free(`object`)
    }

    fun destroy_all(cb: (Bonus)->Unit = {}) {
        used_bonus.forEach { bonus ->
            cb(bonus)
            bonus.addAction(Actions.run { bonus.destroy {} })
        }
        used_bonus.clear()
    }
}
