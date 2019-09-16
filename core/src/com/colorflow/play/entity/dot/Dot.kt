package com.colorflow.play.entity.dot

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.Path
import com.colorflow.utils.AssetProvider
import com.colorflow.utils.Color
import com.colorflow.utils.Position

class Dot(assets: AssetProvider, pool: Pool<Dot>) : Entity(assets, pool as Pool<Entity>) {
    lateinit var type: Type
        protected set
    lateinit var colour: Color
        protected set

    fun set(type: Type, color: Color, pathType: Path.Type, start: Position.Radial, velocity: Float) {
        this.type = type
        this.colour = color
        when (type) {
            Dot.Type.STD -> this.texture = _assets.get_texture("dot_std")
            Dot.Type.REVERSE -> this.texture = _assets.get_texture("dot_reverse")
            Dot.Type.COIN -> this.texture = _assets.get_texture("dot_coin")
        }
        this.initTrail(color)
        path.type = pathType
        path.pos.x = start.x
        path.pos.y = start.y
        path.velocity = velocity
        this.bounds.setRadius(40f)
        super.set()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch!!.color = colour.rgb
        super.draw(batch, parentAlpha)
        batch.color = com.badlogic.gdx.graphics.Color.WHITE
    }

    override fun act(delta: Float) {
        rotateBy(1.5f)
        super.act(delta)
    }

    override fun destroy(cb: (Entity)->Unit) {
        _pool.free(this)
        super.destroy(cb)
    }

    override fun dispose() {
        super.dispose()
    }

    override fun reset() {}

    private fun initTrail(color: Color) {
        val colors = this.trail.emitters.first().tint.colors
        colors[0] = color.rgb.r
        colors[1] = color.rgb.g
        colors[2] = color.rgb.b
    }

    enum class Type {
        STD, REVERSE, COIN
    }
}

