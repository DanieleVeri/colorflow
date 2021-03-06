package com.colorflow.graphic.effects.particle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import com.colorflow.AssetProvider
import com.colorflow.graphic.Position

class Particle(assets: AssetProvider,
        protected val pool: Pool<Particle>) : Actor(), Disposable, Pool.Poolable {

    private val effect: ParticleEffect = ParticleEffect(assets.get_particles("exp"))

    fun start(color: Color, position: Position) {
        effect.reset()
        val colors = effect.emitters.first().tint.colors
        colors[0] = color.r
        colors[1] = color.g
        colors[2] = color.b
        effect.setPosition(position.x, position.y)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        effect.draw(batch)
    }

    override fun act(delta: Float) {
        if (effect.isComplete) {
            pool.free(this)
            this.remove()
        }
        effect.update(delta)
        super.act(delta)
    }

    override fun dispose() {
        effect.dispose()
    }

    override fun reset() {}
}