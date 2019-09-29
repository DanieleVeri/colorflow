package com.colorflow.stage

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.graphic.Position
import com.colorflow.graphic.effects.EffectLayer
import com.colorflow.graphic.effects.ExplosionPool

open class EffectStage(viewport: Viewport) : Stage(viewport) {
    protected val effect_layer: EffectLayer
    protected val explosion_pool: ExplosionPool

    init {
        effect_layer = EffectLayer()
        explosion_pool = ExplosionPool()
        super.addActor(effect_layer)
    }

    override fun addActor(actor: Actor?) {
        effect_layer.addActor(actor)
    }

    override fun clear() {
        effect_layer.clear()
    }

    override fun getActors(): Array<Actor> {
        return effect_layer.children
    }

    protected fun shockwave(position: Position) {
        effect_layer.shockwave(position.x, position.y)
    }

    protected fun explosion(color: Color, position: Position) {
        val obj = explosion_pool.get()
        addActor(obj)
        obj.start(color, position)
    }

    protected fun set_bg_color(color: Color) {
        effect_layer.background_color = color
    }

    override fun dispose() {
        effect_layer.dispose()
        explosion_pool.clear()
        super.dispose()
    }

}