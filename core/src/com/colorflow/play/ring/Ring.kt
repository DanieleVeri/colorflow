package com.colorflow.play.ring

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.colorflow.AssetProvider
import com.colorflow.utils.Color
import com.colorflow.utils.Position

class Ring(asset_provider: AssetProvider, ring_id: String) : Actor() {

    private var texture: TextureRegion
    val circle: Circle
    private var radius: Float = 0f
    private var rotation = 0f
    private val listener: RingListener

    init {
        this.texture = asset_provider.get_skin("play_stage").getRegion(ring_id)
        this.radius = texture.regionWidth / 2f
        setBounds(Position.widthScreen / 2 - texture.regionWidth / 2, Position.heightScreen / 2 - texture.regionHeight / 2,
                texture.regionWidth.toFloat(), texture.regionHeight.toFloat())
        this.circle = Circle(Position.center.x, Position.center.y, radius)
        this.listener = SideTapListener()
    }

    override fun draw(batch: Batch?, alpha: Float) {
        batch!!.draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, getRotation())
    }

    override fun act(delta: Float) {
        addAction(listener.onRingAct())
        super.act(delta)
    }

    fun getColorFor(angle: Float): Color {
        val range_angle = Position.Radial.regulate_angle(angle + getRotation())
        return when (range_angle) {
            in 0.0..60.0 -> Color.BLUE
            in 60.0..120.0 -> Color.MAGENTA
            in 120.0..180.0 -> Color.GREEN
            in 180.0..240.0 -> Color.YELLOW
            in 240.0..300.0 -> Color.RED
            in 300.0..360.0 -> Color.CYAN
            else -> Color.BLUE
        }
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