package com.colorflow.engine.entity.dot

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Pool
import com.colorflow.AssetProvider
import com.colorflow.graphic.Color
import com.colorflow.graphic.Position

class DotPool(protected val assets: AssetProvider) : Pool<Dot>() {
    protected val used_dots: ArrayList<Dot> = ArrayList()

    override fun newObject(): Dot {
        return Dot(assets, this)
    }

    fun get(type: Dot.Type, color: Color, position: Position.Radial): Dot {
        val d = obtain()
        d.set(type, color, position)
        used_dots.add(d)
        return d
    }

    override fun free(`object`: Dot?) {
        used_dots.remove(`object`)
        super.free(`object`)
    }

    fun destroy_all(cb: (Dot)->Unit = {}) {
        used_dots.forEach { dot ->
            cb(dot)
            dot.addAction(Actions.run { dot.destroy {} })
        }
        used_dots.clear()
    }
}
