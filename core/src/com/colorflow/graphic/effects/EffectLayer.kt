package com.colorflow.graphic.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.colorflow.AssetProvider
import com.colorflow.graphic.Position

class EffectLayer: Group() {

    fun shockwave(position: Position) {
        val v = Vector2(position.x, position.y)
        v.x = v.x / Gdx.graphics.width
        v.y = v.y / Gdx.graphics.height
        manager.get_effect("shockwave").start(1f) { shader ->
            shader.setUniformf("center", v)
        }
    }

    fun twinkling() {
        manager.get_effect("twinkling").start(1f)
    }

    fun glow(position: Position = Position.center) {
        manager.get_effect("glow").start(1f)
    }

    fun spectrum(cb: (ShaderProgram) -> Unit) {
        manager.get_bg_effect("spectrum").start(Float.POSITIVE_INFINITY, cb)
    }

    fun fractal(cb: (ShaderProgram) -> Unit) {
        manager.get_bg_effect("fractal").start(Float.POSITIVE_INFINITY, cb)
    }

    fun explosion(color: Color, position: Position) {
        val obj = manager.get_explosion_particle()
        addActor(obj)
        obj.start(color, position)
    }

    fun stop_all() {
        manager.stop_all_effects()
    }

    override fun act(delta: Float) {
        manager.update_effects(delta)
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(batch == null) {
            Gdx.app.error(this::class.java.simpleName, "null batch: skipping draw")
            return
        }

        batch.end()
        batch.flush()
        batch.begin()

        Gdx.gl.glClearColor(0f, 0f, 0f, parentAlpha)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // background effects
        manager.fbo.begin()
        var texture = manager.fbo.colorBufferTexture
        texture = manager.apply_bg_effects(batch, texture)
        batch.draw(texture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
        super.draw(batch, parentAlpha)
        batch.flush()
        manager.fbo.end()

        // after effects
        texture = manager.fbo.colorBufferTexture
        texture = manager.apply_effects(batch, texture)
        batch.draw(texture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
    }

    companion object {
        private lateinit var manager: EffectManager
        fun init(asset: AssetProvider) {
            manager = EffectManager(asset)
        }
        fun dispose() {
            manager?.dispose()
        }
    }

}
