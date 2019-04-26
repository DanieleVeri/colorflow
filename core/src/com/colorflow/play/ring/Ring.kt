package com.colorflow.play.ring

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.colorflow.utils.Color
import com.colorflow.utils.Position

class Ring(img_name: String) : Actor(), Disposable {

    private var texture: Texture? = null
    val circle: Circle
    private var radius: Float = 0.toFloat()
    private var rotation = 0f
    private val listener: RingListener

    init {
        this.texture = Texture(Gdx.files.local("rings/$img_name"))
        this.radius = 150f
        setBounds(Position.widthScreen / 2 - texture!!.width / 2, Position.heightScreen / 2 - texture!!.height / 2,
                texture!!.width.toFloat(), texture!!.height.toFloat())
        this.circle = Circle(Position.center.x, Position.center.y, radius)
        this.listener = SideTapListener()
    }

    override fun draw(batch: Batch?, alpha: Float) {
        batch!!.draw(texture, x, y, width / 2, height / 2, width, height,
                scaleX, scaleY, getRotation(), 0, 0, width.toInt(), height.toInt(), false, true)
    }

    override fun act(delta: Float) {
        addAction(listener.onRingAct())
        super.act(delta)
    }

    override fun dispose() {
        texture!!.dispose()
    }

    fun getColorFor(angle: Float): Color {
        val range_angle = Position.Radial.regulate_angle(angle + getRotation())
        if (range_angle in 0.0..60.0) {
            return Color.CYAN
        } else if (range_angle in 60.0..120.0) {
            return Color.RED
        } else if (range_angle in 120.0..180.0) {
            return Color.YELLOW
        } else if (range_angle in 180.0..240.0) {
            return Color.GREEN
        } else if (range_angle in 240.0..300.0) {
            return Color.MAGENTA
        } else if (range_angle in 300.0..360.0) {
            return Color.BLUE
        }
        return Color.BLUE
    }

    override fun getRotation(): Float {
        return rotation
    }

    override fun setRotation(rotation: Float) {
        this.rotation = Position.Radial.regulate_angle(rotation)
    }

    override fun rotateBy(amountInDegrees: Float) {
        setRotation(getRotation() + amountInDegrees)
    }

    override fun setScale(scaleXY: Float) {
        super.setScale(scaleXY)
        radius *= scaleXY
    }

    fun getListener(): InputProcessor {
        return listener
    }
}