package com.colorflow.utils

import com.badlogic.gdx.Gdx

open class Position protected constructor(open var x: Float, open var y: Float) {

    val distRadial: Float
        get() = Math.sqrt(xCartesian * xCartesian + yCartesian * yCartesian).toFloat()

    val angleRadial: Float
        get() {
            val alpha = Math.toDegrees(Math.atan2(yCartesian, xCartesian))
            return (if (alpha > 0) alpha else alpha + 360).toFloat()
        }

    private val xCartesian: Double
        get() = x - widthScreen / 2.0

    private val yCartesian: Double
        get() = heightScreen / 2.0 - y

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (other === this)
            return true
        if (other !is Position)
            return false
        val other = other as Position?
        return x == other!!.x && y == other.y
    }

    class Pixel(x: Float, y: Float) : Position(x, y)

    class Radial(angle: Float, dist: Float) : Position((Math.cos(Math.toRadians(angle.toDouble())) * dist + widthScreen / 2.0).toFloat(), (heightScreen / 2.0 - Math.sin(Math.toRadians(angle.toDouble())) * dist).toFloat()) {

        fun set_angle(angle: Float) {
            val dist = distRadial.toDouble()
            super.x = (Math.cos(Math.toRadians(angle.toDouble())) * dist + widthScreen / 2.0).toFloat()
            super.y = (heightScreen / 2.0 - Math.sin(Math.toRadians(angle.toDouble())) * dist).toFloat()
        }

        fun set_dist(dist: Float) {
            val angle = Math.toRadians(angleRadial.toDouble())
            super.x = (Math.cos(angle) * dist + widthScreen / 2.0).toFloat()
            super.y = (heightScreen / 2.0 - Math.sin(angle) * dist).toFloat()
        }

        companion object {

            fun regulate_angle(value: Float): Float {
                var range_val = value
                while (range_val < 0) range_val += 360
                while (range_val >= 360) range_val -= 360
                return range_val
            }
        }
    }

    companion object {
        var widthScreen = Gdx.graphics.width.toFloat()
            protected set
        var heightScreen = Gdx.graphics.height.toFloat()
            protected set
        val center: Position
            get() = Position(widthScreen / 2.0f, heightScreen / 2.0f)
    }

}
