package com.colorflow.engine.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import com.colorflow.AssetProvider
import com.colorflow.engine.EntityCoordinator
import com.colorflow.graphic.Position

abstract class Entity (
        protected val assets: AssetProvider) : Actor(), Pool.Poolable, Disposable {

    lateinit var coordinator: IMotionCoordinator
    var position: Position.Radial; protected set
    var bounds: Circle; protected set
    protected var trail: ParticleEffect
    protected lateinit var texture: TextureRegion

    init {
        position = Position.Radial(0f, 0f)
        bounds = Circle(0f, 0f, 1f)
        trail = ParticleEffect()
        trail.load(Gdx.files.internal("sprites/trail.p"), Gdx.files.internal("sprites"))
    }

    fun set() {
        setBounds(position.x - texture.regionWidth / 2,
                position.y - texture.regionHeight / 2,
                texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
        bounds.setPosition(position.x, position.y)
        trail.reset()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        trail.draw(batch)
        batch!!.draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation)
    }

    abstract fun next_pos(delta: Float)

    override fun act(delta: Float) {
        /* Updating position */
        next_pos(delta)
        bounds.setPosition(position.x, position.y)
        addAction(Actions.moveTo(
                position.x - texture.regionWidth / 2,
                position.y - texture.regionHeight / 2))

        /* Trail */
        trail.setPosition(position.x, position.y)
        val angle = position.angleRadial
        trail.emitters.first().angle.setHigh(angle - 45, angle + 45)
        trail.emitters.first().angle.setLow(angle - 45, angle + 45)
        trail.flipY()
        trail.update(delta)

        /* Processing actions */
        super.act(delta)
        if (!isVisible) {
            isVisible = true
        }
    }

    open fun destroy(cb: (Entity)->Unit) {
        isVisible = false
        cb(this)
        super.remove()
    }

    override fun dispose() {
        trail.dispose()
    }

    abstract override fun reset()

}
