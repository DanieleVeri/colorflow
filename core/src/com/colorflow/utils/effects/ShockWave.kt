package com.colorflow.utils.effects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction

class ShockWave private constructor() : Group() {
    private val fbo: FrameBuffer
    private val vertexShader: String
    private val fragmentShader: String
    private val shaderProgram: ShaderProgram
    private var time: Float = 0.toFloat()

    private var disabled: Boolean = false

    private var shockWavePositionX: Float = 0.toFloat()
    private var shockWavePositionY: Float = 0.toFloat()

    init {
        disabled = true
        time = 0f
        vertexShader = Gdx.files.internal("shaders/vertex.glsl").readString()
        fragmentShader = Gdx.files.internal("shaders/fragment.glsl").readString()
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)
        ShaderProgram.pedantic = false

        fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    }

    fun start(posX: Float, posY: Float) {
        this.shockWavePositionX = posX
        this.shockWavePositionY = posY
        val enable = RunnableAction()
        enable.runnable = Runnable { disabled = true }
        this.addAction(Actions.delay(1f, enable)) // last of animation: 1 sec
        disabled = false
        time = 0f
    }

    override fun act(delta: Float) {
        super.act(delta)
        time += delta
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (disabled) {
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
            super.draw(batch, parentAlpha)
        } else {
            batch!!.end()
            batch.flush()
            fbo.begin()
            batch.begin()
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)
            super.draw(batch, parentAlpha)
            batch.end()
            batch.flush()
            fbo.end()
            batch.begin()
            batch.shader = shaderProgram
            val v = Vector2(shockWavePositionX, shockWavePositionY)
            v.x = v.x / Gdx.graphics.width
            v.y = v.y / Gdx.graphics.height
            shaderProgram.setUniformf("time", time)
            shaderProgram.setUniformf("center", v)
            val texture = fbo.colorBufferTexture
            val textureRegion = TextureRegion(texture)
            batch.draw(textureRegion, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            batch.shader = null
        }
    }

    companion object {

        private var shockWave: ShockWave? = null

        val instance: ShockWave
            get() {
                if (shockWave == null) {
                    shockWave = ShockWave()
                }
                return shockWave!!
            }
    }
}
