package com.colorflow.engine.entity.bonus

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.colorflow.engine.entity.Entity
import com.colorflow.AssetProvider
import com.colorflow.graphic.Position

class Bonus(assets: AssetProvider,
            protected val pool: BonusPool) : Entity(assets) {

    lateinit var type: Type; protected set
    protected var trail: ParticleEffect = ParticleEffect(assets.get_particles("trail"))

    fun set(type: Type, position: Position.Radial) {
        this.type = type
        val colors = this.trail.emitters.first().tint.colors
        colors[0] = 1f
        colors[1] = 1f
        colors[2] = 1f
        when (type) {
            Type.BOMB -> this.texture = assets.get_skin("game").atlas.findRegion("bonus_bomb")
            Type.GOLD -> this.texture = assets.get_skin("game").atlas.findRegion("dot_coin")
            Type.TODO -> null
        }
        this.position.x = position.x
        this.position.y = position.y
        this.bounds.setRadius(texture.regionWidth/2f)
        trail.reset()
        super.set()
    }

    override fun act(delta: Float) {
        // Trail
        trail.setPosition(position.x, position.y)
        val angle = position.angleRadial
        trail.emitters.first().angle.setHigh(angle - 45, angle + 45)
        trail.emitters.first().angle.setLow(angle - 45, angle + 45)
        trail.flipY()
        trail.update(delta)

        rotateBy(1.5f)
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        trail.draw(batch)
        super.draw(batch, parentAlpha)
    }

    override fun destroy(cb: (Entity)->Unit) {
        pool.free(this)
        super.destroy(cb)
    }

    override fun dispose() {
        trail.dispose()
    }

    override fun next_pos(delta: Float) {
        position.set_dist(position.distRadial - coordinator.bonus_velocity)
    }

    override fun reset() {}

    enum class Type {
        BOMB, GOLD, TODO
    }
}
