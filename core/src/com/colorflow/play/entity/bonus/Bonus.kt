package com.colorflow.play.entity.bonus

import com.badlogic.gdx.utils.Pool
import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.Path
import com.colorflow.utils.AssetProvider
import com.colorflow.utils.Position

class Bonus(assets: AssetProvider, pool: Pool<Bonus>) : Entity(assets, pool as Pool<Entity>) {
    lateinit var type: Type
        protected set

    fun set(type: Type, pathType: Path.Type, start: Position.Radial, velocity: Float) {
        this.type = type
        val colors = this.trail.emitters.first().tint.colors
        colors[0] = 1f
        colors[1] = 1f
        colors[2] = 1f
        when (type) {
            Bonus.Type.BOMB -> this.texture = _assets.get_texture("bonus_bomb")
            else -> throw IllegalStateException()
        }
        path.type = pathType
        path.pos = start
        path.velocity = velocity
        this.bounds.setRadius(40f)
        super.set()
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

    enum class Type {
        BOMB, GOLD, MAGNETIC
    }
}
