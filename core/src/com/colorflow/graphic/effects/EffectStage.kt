package com.colorflow.graphic.effects

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport

open class EffectStage(viewport: Viewport) : Stage(viewport) {
    protected val effect_layer: EffectLayer

    init {
        effect_layer = EffectLayer()
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

}