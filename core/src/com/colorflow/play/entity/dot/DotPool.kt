package com.colorflow.play.entity.dot

import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Path
import com.colorflow.utils.AssetProvider
import com.colorflow.utils.Color
import com.colorflow.utils.Position

class DotPool(private val assets: AssetProvider) : Pool<Dot>() {

    override fun newObject(): Dot {
        return Dot(assets, this)
    }

    fun get(type: Dot.Type, color: Color, pathType: Path.Type, start: Position.Radial, speed: Float): Dot {
        val d = obtain()
        d.set(type, color, pathType, start, speed)
        return d
    }
}
