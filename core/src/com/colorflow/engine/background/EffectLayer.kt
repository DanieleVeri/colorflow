package com.colorflow.engine.background

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Disposable
import com.colorflow.graphic.Position

class EffectLayer: Group(), Disposable {
    protected var fbo: FrameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    protected val shader_effects: ArrayList<ShaderEffect> = ArrayList()

    init {
        shader_effects.add(ShaderEffect("shockwave"))
        shader_effects.add(ShaderEffect("twinkling"))
        shader_effects.add(ShaderEffect("glow"))
    }

    fun shockwave(position: Position) {
        val v = Vector2(position.x, position.y)
        v.x = v.x / Gdx.graphics.width
        v.y = v.y / Gdx.graphics.height
        shader_effects.find { it.fragment == "shockwave" }!!.start(1f) { shader ->
            shader.setUniformf("center", v)
        }
    }

    fun twinkling() {
        shader_effects.find { it.fragment == "twinkling" }!!.start(1f)
    }

    fun glow(position: Position = Position.center) {
        shader_effects.find { it.fragment == "glow" }!!.start(1f)
    }

    override fun dispose() {
        fbo.dispose()
        shader_effects.forEach { it.dispose() }
    }

    override fun act(delta: Float) {
        shader_effects.forEach { it.update(delta) }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch ?: return
        batch.end()
        batch.flush()
        fbo.begin()
        batch.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
        super.draw(batch, parentAlpha)
        batch.flush()
        fbo.end()

        var current = fbo.colorBufferTexture
        shader_effects.forEach {
            current = it.apply(batch, current)
        }
        batch.draw(current,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
    }

}
