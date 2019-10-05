package com.colorflow.engine.entity.dot

import com.badlogic.gdx.graphics.g2d.Batch
import com.colorflow.engine.entity.Entity
import com.colorflow.AssetProvider
import com.colorflow.engine.EntityCoordinator
import com.colorflow.engine.entity.IMotionCoordinator
import com.colorflow.graphic.Color
import com.colorflow.graphic.Position

class Dot(assets: AssetProvider,
          protected val pool: DotPool) : Entity(assets) {

    lateinit var type: Type; protected set
    lateinit var colour: Color; protected set

    fun set(type: Type, color: Color, position: Position.Radial) {
        this.type = type
        this.colour = color
        when (type) {
            Type.STD -> this.texture = assets.get_skin("play_stage").atlas.findRegion("dot_std")
            Type.REVERSE -> this.texture = assets.get_skin("play_stage").atlas.findRegion("dot_reverse")
        }
        this.initTrail(color)
        this.position.x = position.x
        this.position.y = position.y
        this.bounds.setRadius(texture.regionWidth/2f)
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
        pool.free(this)
        super.destroy(cb)
    }

    override fun next_pos(delta: Float) {
        when (coordinator.path_type) {
            IMotionCoordinator.PathType.RADIAL -> position.set_dist(position.distRadial - coordinator.dot_velocity)
            IMotionCoordinator.PathType.SPIRAL -> {
                position.set_dist(position.distRadial - coordinator.dot_velocity)
                position.set_angle(position.angleRadial - 1f)
            }
        }
    }

    override fun reset() {}

    private fun initTrail(color: Color) {
        val colors = this.trail.emitters.first().tint.colors
        colors[0] = color.rgb.r
        colors[1] = color.rgb.g
        colors[2] = color.rgb.b
    }

    enum class Type {
        STD, REVERSE
    }
}

