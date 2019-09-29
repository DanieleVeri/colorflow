package com.colorflow.graphic.effects

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
    var background_color: Color = Color.BLACK
    private val fbo: FrameBuffer

    private var shockwave_time: Float
    private val shockwave_shader: ShaderProgram
    private var shockwave_disabled: Boolean
    private var shockwave_x: Float = 0f
    private var shockwave_y: Float = 0f

    init {
        shockwave_disabled = true
        shockwave_time = 0f
        val vertexShader = Gdx.files.internal("shaders/vertex.glsl").readString()
        val fragmentShader = Gdx.files.internal("shaders/fragment.glsl").readString()
        shockwave_shader = ShaderProgram(vertexShader, fragmentShader)
        ShaderProgram.pedantic = false
        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    }

    fun shockwave(posX: Float, posY: Float) {
        shockwave_x = posX
        shockwave_y = posY
        val enable = RunnableAction()
        enable.runnable = Runnable { shockwave_disabled = true }
        this.addAction(Actions.delay(1f, enable)) // last of animation: 1 sec
        shockwave_disabled = false
        shockwave_time = 0f
    }

    override fun act(delta: Float) {
        super.act(delta)
        shockwave_time += delta
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (shockwave_disabled) {
            bg_clear()
            super.draw(batch, parentAlpha)
            return
        }
        batch!!.end()
        batch.flush()
        fbo.begin()
        batch.begin()
        bg_clear()
        super.draw(batch, parentAlpha)
        batch.end()
        batch.flush()
        fbo.end()

        batch.begin()
        batch.shader = shockwave_shader
        val v = Vector2(shockwave_x, shockwave_y)
        v.x = v.x / Gdx.graphics.width
        v.y = v.y / Gdx.graphics.height
        shockwave_shader.setUniformf("time", shockwave_time)
        shockwave_shader.setUniformf("center", v)
        val resolution = Vector2(Position.widthScreen, Position.heightScreen)
        shockwave_shader.setUniformf("resolution", resolution)
        batch.draw(fbo.colorBufferTexture,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
        batch.shader = null
    }

    protected fun bg_clear() {
        Gdx.gl.glClearColor(background_color.r, background_color.g, background_color.b,
                background_color.a)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
    }
    override fun dispose() {
        fbo.dispose()
        shockwave_shader.dispose()
    }

}
