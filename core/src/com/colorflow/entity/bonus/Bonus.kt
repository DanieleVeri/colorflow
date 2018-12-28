package com.colorflow.entity.bonus

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.colorflow.entity.Entity
import com.colorflow.entity.Path
import com.colorflow.entity.trigger.Trigger
import com.colorflow.utility.Position
import com.colorflow.utility.effect.ExplosionPool

class Bonus : Entity() {
    lateinit var type: Type
        protected set

    operator fun set(type: Type, pathType: Path.Type, start: Position.Radial, speed: Float) {
        this.type = type
        val colors = this.trail.emitters.first().tint.colors
        colors[0] = 1f
        colors[1] = 1f
        colors[2] = 1f
        when (type) {
            Bonus.Type.BOMB -> this.texture = bombTexture
            else -> throw IllegalStateException()
        }
        this.path.set(pathType, start, speed)
        this.bounds.setRadius(40f)
        super.set()
    }

    override fun act(delta: Float) {
        rotateBy(1.5f)
        super.act(delta)
    }

    override fun destroy(trigger: Trigger?) {
        ExplosionPool.getInstance().start(stage, Color.WHITE, position)
        BonusPool.instance.free(this)
        super.destroy(trigger)
    }

    override fun dispose() {
        super.dispose()
    }

    override fun reset() {}

    enum class Type {
        BOMB, GOLD, MAGNETIC
    }

    companion object {

        private val bombTexture = Texture("bonus/bomb.png")
    }
}
