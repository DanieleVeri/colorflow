package com.colorflow.engine.background

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.colorflow.graphic.Position

class ShaderEffect(fragment: String, vertex: String = "vertex"): Disposable {
    protected val shader_program: ShaderProgram
    protected lateinit var set_uniform: (ShaderProgram) -> Unit
    protected val fbo: FrameBuffer
    protected var time: Float
    protected var last: Float

    init {
        val vertex_shader = Gdx.files.internal("shaders/$vertex.glsl").readString()
        val fragment_shader = Gdx.files.internal("shaders/${fragment}_fragment.glsl").readString()
        shader_program = ShaderProgram(vertex_shader, fragment_shader)
        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
        time = 0f
        last = 0f
    }

    override fun dispose() {
        fbo.dispose()
        shader_program.dispose()
    }

    fun update(delta: Float) {
        time += delta
    }

    fun apply(batch: Batch, current: Texture): Texture {
        if(time > last) return current
        batch.shader = shader_program
        shader_program.setUniformf("resolution", resolution)
        shader_program.setUniformf("time", time)
        set_uniform(shader_program)
        fbo.begin()
        batch.draw(current,
                0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                0, 0, Gdx.graphics.width, Gdx.graphics.height,
                false, true)
        fbo.end()
        batch.shader = null
        return fbo.colorBufferTexture
    }

    fun start(last: Float, set_uniform: (ShaderProgram)->Unit = {}) {
        this.set_uniform = set_uniform
        this.last = last
        time = 0f
    }

    companion object {
        init {
            ShaderProgram.pedantic = false
        }
        val resolution = Vector2(Position.widthScreen, Position.heightScreen)
    }
}