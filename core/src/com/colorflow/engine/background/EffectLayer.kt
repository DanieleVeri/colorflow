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
    private val resolution = Vector2(Position.widthScreen, Position.heightScreen)

    var background_color: Color = Color.BLACK
    val arcs = Arcs()

    private val fbo_base: FrameBuffer
    private val fbo_shockwave: FrameBuffer
    private val fbo_twinkling: FrameBuffer
    private val fbo_glow: FrameBuffer

    private val shockwave_shader: ShaderProgram
    private val twinkling_shader: ShaderProgram
    private val glow_shader: ShaderProgram

    private var shockwave_time: Float = 0f
    private var shockwave_disabled: Boolean = true
    private var shockwave_x: Float = 0f
    private var shockwave_y: Float = 0f

    private var twinkling_time: Float = 0f
    private var twinkling_disabled: Boolean = true

    private var glow_time: Float = 0f
    private var glow_disabled: Boolean = true

    init {
        ShaderProgram.pedantic = false

        val vertex_shader = Gdx.files.internal("shaders/vertex.glsl").readString()
        val twinkling_fragment_shader = Gdx.files.internal("shaders/twinkling_fragment.glsl").readString()
        val shockwave_fragment_shader = Gdx.files.internal("shaders/shockwave_fragment.glsl").readString()
        val glow_fragment_shader = Gdx.files.internal("shaders/glow_fragment.glsl").readString()

        shockwave_shader = ShaderProgram(vertex_shader, shockwave_fragment_shader)
        twinkling_shader = ShaderProgram(vertex_shader, twinkling_fragment_shader)
        glow_shader = ShaderProgram(vertex_shader, glow_fragment_shader)

        fbo_base = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        fbo_shockwave = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        fbo_twinkling = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        fbo_glow = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    }

    override fun dispose() {
        fbo_base.dispose()
        fbo_shockwave.dispose()
        fbo_twinkling.dispose()
        fbo_glow.dispose()
        shockwave_shader.dispose()
        twinkling_shader.dispose()
        glow_shader.dispose()
    }

    override fun act(delta: Float) {
        shockwave_time += delta
        twinkling_time += delta
        glow_time += delta
        arcs.act(delta)
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch ?: return

        batch.end()
        batch.flush()
        fbo_base.begin()
        batch.begin()
        clear_background()
        arcs.draw(batch, parentAlpha)
        super.draw(batch, parentAlpha)
        batch.flush()
        fbo_base.end()
        var current = fbo_base.colorBufferTexture

        if(!shockwave_disabled) {
            batch.shader = shockwave_shader
            fbo_shockwave.begin()
            val v = Vector2(shockwave_x, shockwave_y)
            v.x = v.x / Gdx.graphics.width
            v.y = v.y / Gdx.graphics.height
            shockwave_shader.setUniformf("time", shockwave_time)
            shockwave_shader.setUniformf("center", v)
            shockwave_shader.setUniformf("resolution", resolution)
            batch.draw(current,
                    0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                    0, 0, Gdx.graphics.width, Gdx.graphics.height,
                    false, true)
            batch.shader = null
            fbo_shockwave.end()
            current = fbo_shockwave.colorBufferTexture
        }

        if(!twinkling_disabled) {
            batch.shader = twinkling_shader
            fbo_twinkling.begin()
            twinkling_shader.setUniformf("time", twinkling_time)
            twinkling_shader.setUniformf("resolution", resolution)
            batch.draw(current,
                    0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                    0, 0, Gdx.graphics.width, Gdx.graphics.height,
                    false, true)
            batch.shader = null
            fbo_twinkling.end()
            current = fbo_twinkling.colorBufferTexture
        }

        if(!glow_disabled) {
            batch.shader = glow_shader
            fbo_glow.begin()
            glow_shader.setUniformf("time", glow_time)
            glow_shader.setUniformf("resolution", resolution)
            batch.draw(current,
                    0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                    0, 0, Gdx.graphics.width, Gdx.graphics.height,
                    false, true)
            batch.shader = null
            fbo_glow.end()
            current = fbo_glow.colorBufferTexture
        }

        batch.draw(current,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
    }

    fun shockwave(position: Position) {
        shockwave_x = position.x
        shockwave_y = position.y
        val action = RunnableAction()
        action.runnable = Runnable { shockwave_disabled = true }
        addAction(Actions.delay(1f, action)) // last of animation: 1 sec
        shockwave_disabled = false
        shockwave_time = 0f
    }

    fun twinkling() {
        val action = RunnableAction()
        action.runnable = Runnable { twinkling_disabled = true }
        addAction(Actions.delay(1f, action)) // last of animation: 1 sec
        twinkling_disabled = false
        twinkling_time = 0f
    }

    fun glow(position: Position = Position.center) {
        val action = RunnableAction()
        action.runnable = Runnable { glow_disabled = true }
        addAction(Actions.delay(1f, action)) // last of animation: 1 sec
        glow_disabled = false
        glow_time = 0f
    }

    protected fun clear_background() {
        Gdx.gl.glClearColor(background_color.r, background_color.g, background_color.b, 1f)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
    }

}
