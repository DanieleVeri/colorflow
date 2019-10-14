package com.colorflow.graphic.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.colorflow.graphic.Position

class EffectLayer: Group() {

    fun shockwave(position: Position) {
        val v = Vector2(position.x, position.y)
        v.x = v.x / Gdx.graphics.width
        v.y = v.y / Gdx.graphics.height
        Effects.shader_effects.find { it.fragment == "shockwave" }!!.start(1f) { shader ->
            shader.setUniformf("center", v)
        }
    }

    fun twinkling() {
        Effects.shader_effects.find { it.fragment == "twinkling" }!!.start(1f)
    }

    fun glow(position: Position = Position.center) {
        Effects.shader_effects.find { it.fragment == "glow" }!!.start(1f)
    }

    fun explosion(color: Color, position: Position) {
        val obj = Effects.explosion_pool.get()
        addActor(obj)
        obj.start(color, position)
    }

    fun stop_all() {
        Effects.shader_effects.forEach { it.stop() }
    }

    override fun act(delta: Float) {
        Effects.shader_effects.forEach { it.update(delta) }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(batch == null) {
            Gdx.app.error(this::class.java.simpleName, "null batch: skipping draw")
            return
        }
        batch.end()
        batch.flush()
        Effects.fbo.begin()
        batch.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, parentAlpha)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.draw(batch, parentAlpha)
        batch.flush()
        Effects.fbo.end()

        var current = Effects.fbo.colorBufferTexture
        Effects.shader_effects.forEach {
            current = it.apply(batch, current)
        }

        batch.draw(current,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
    }

}
