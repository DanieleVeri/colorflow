package com.colorflow.engine.entity.bonus

import com.colorflow.engine.entity.Entity
import com.colorflow.AssetProvider
import com.colorflow.graphic.Position

class Bonus(assets: AssetProvider,
            protected val pool: BonusPool) : Entity(assets) {

    lateinit var type: Type; protected set

    fun set(type: Type, position: Position.Radial) {
        this.type = type
        val colors = this.trail.emitters.first().tint.colors
        colors[0] = 1f
        colors[1] = 1f
        colors[2] = 1f
        when (type) {
            Type.BOMB -> this.texture = assets.get_skin("play_stage").atlas.findRegion("bonus_bomb")
            Type.GOLD -> this.texture = assets.get_skin("play_stage").atlas.findRegion("dot_coin")
            Type.TODO -> null
        }
        this.position.x = position.x
        this.position.y = position.y
        this.bounds.setRadius(texture.regionWidth/2f)
        super.set()
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
        position.set_dist(position.distRadial - coordinator.bonus_velocity)
    }

    override fun reset() {}

    enum class Type {
        BOMB, GOLD, TODO
    }
}
