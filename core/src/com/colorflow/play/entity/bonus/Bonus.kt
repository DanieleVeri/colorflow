package com.colorflow.play.entity.bonus

import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.Path
import com.colorflow.AssetProvider
import com.colorflow.graphic.Position

class Bonus(assets: AssetProvider,
            protected val pool: BonusPool) : Entity(assets) {
    lateinit var type: Type
        protected set

    fun set(type: Type, pathType: Path.Type, start: Position.Radial, velocity: Float) {
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
        path.type = pathType
        path.pos = start
        path.static_velocity = velocity
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

    override fun reset() {}

    enum class Type {
        BOMB, GOLD, TODO
    }
}
