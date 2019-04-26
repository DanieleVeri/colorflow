package com.colorflow.play.entity.dot

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.Path
import com.colorflow.utils.Color
import com.colorflow.utils.Position
import com.colorflow.utils.effects.ExplosionPool

class Dot : Entity() {
    lateinit var type: Type
        protected set
    lateinit var colour: Color
        protected set

    operator fun set(type: Type, color: Color, pathType: Path.Type, start: Position.Radial, speed: Float) {
        this.type = type
        this.colour = color
        when (type) {
            Dot.Type.STD -> this.texture = stdTexture
            Dot.Type.REVERSE -> this.texture = reverseTexture
            Dot.Type.COIN -> this.texture = coinTexture
        }
        this.initTrail(color)
        this.path[pathType, start] = speed
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
        ExplosionPool.instance.start(stage, colour.rgb, position)
        DotPool.instance.free(this)
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

    companion object {
        private val stdTexture = Texture("dots/std.png")
        private val reverseTexture = Texture("dots/reverse.png")
        private val coinTexture = Texture("dots/coin.png")
    }
}

