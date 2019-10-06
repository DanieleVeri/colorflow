package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.engine.background.Arcs.Companion.MAX_VISIBLE
import com.colorflow.graphic.Position
import com.colorflow.engine.background.EffectLayer
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

    override fun dispose() {
        effect_layer.dispose()
        explosion_pool.clear()
        super.dispose()
    }

    protected fun explosion(color: Color, position: Position) {
        val obj = explosion_pool.get()
        addActor(obj)
        obj.start(color, position)
    }

    protected fun arc_fadein() {
        val action = RunnableAction()
        action.runnable = Runnable {
            if(effect_layer.arcs.radius_offset <= 0)
                return@Runnable
            effect_layer.arcs.radius_offset -= 3f
            effect_layer.arcs.addAction(Actions.delay(0.03f, action))
        }
        effect_layer.arcs.addAction(Actions.delay(0.03f, action))
    }

    protected fun arc_fadeout() {
        val action = RunnableAction()
        action.runnable = Runnable {
            if(effect_layer.arcs.radius_offset > MAX_VISIBLE)
                return@Runnable
            effect_layer.arcs.radius_offset += 3f
            effect_layer.arcs.addAction(Actions.delay(0.03f, action))
        }
        effect_layer.arcs.addAction(Actions.delay(0.03f, action))
    }

}