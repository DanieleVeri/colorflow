package com.colorflow.engine.background

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.utils.Disposable
import com.colorflow.graphic.Position

class EffectLayer: Group(), Disposable {

    protected var fbo: FrameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    protected var shader_shockwave: ShaderEffect = ShaderEffect("shockwave")
    protected var shader_twinkling: ShaderEffect = ShaderEffect("twinkling")
    protected var shader_glow: ShaderEffect = ShaderEffect("glow")
    var background_color: Color = Color.BLACK
    val arcs = Arcs()

    override fun dispose() {
        fbo.dispose()
        shader_glow.dispose()
        shader_twinkling.dispose()
        shader_shockwave.dispose()
    }

    override fun act(delta: Float) {
        shader_twinkling.update(delta)
        shader_glow.update(delta)
        shader_shockwave.update(delta)
        arcs.act(delta)
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch ?: return
        batch.end()
        batch.flush()
        fbo.begin()
        batch.begin()
        clear_background()
        arcs.draw(batch, parentAlpha)
        super.draw(batch, parentAlpha)
        batch.flush()
        fbo.end()
        var current = fbo.colorBufferTexture

        current = shader_shockwave.apply(batch, current)
        current = shader_glow.apply(batch, current)
        current = shader_twinkling.apply(batch, current)

        batch.draw(current,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
    }

    fun shockwave(position: Position) {
        val v = Vector2(position.x, position.y)
        v.x = v.x / Gdx.graphics.width
        v.y = v.y / Gdx.graphics.height
        shader_shockwave.start(1f) { shader ->
            shader.setUniformf("center", v)
        }
    }

    fun twinkling() {
        shader_twinkling.start(1f)
    }

    fun glow(position: Position = Position.center) {
        shader_glow.start(1f)
    }

    protected fun clear_background() {
        Gdx.gl.glClearColor(background_color.r, background_color.g, background_color.b, 1f)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
    }

}
