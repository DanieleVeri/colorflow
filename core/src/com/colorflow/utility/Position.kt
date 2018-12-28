package com.colorflow.utility

import com.badlogic.gdx.Gdx

open class Position protected constructor(open var x: Float, open var y: Float) {

    val xPerc: Float
        get() = x / widthScreen * 100.0f

    val yPerc: Float
        get() = y / heightScreen * 100.0f

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

    override fun equals(obj: Any?): Boolean {
        if (obj == null)
            return false
        if (obj === this)
            return true
        if (obj !is Position)
            return false
        val other = obj as Position?
        return x == other!!.x && y == other.y
    }

    class Pixel(x: Float, y: Float) : Position(x, y)

    class Radial(angle: Float, dist: Float) : Position((Math.cos(Math.toRadians(angle.toDouble())) * dist + widthScreen / 2.0).toFloat(), (heightScreen / 2.0 - Math.sin(Math.toRadians(angle.toDouble())) * dist).toFloat()) {

        fun setAngle(angle: Float) {
            val dist = distRadial.toDouble()
            super.x = (Math.cos(Math.toRadians(angle.toDouble())) * dist + widthScreen / 2.0).toFloat()
            super.y = (heightScreen / 2.0 - Math.sin(Math.toRadians(angle.toDouble())) * dist).toFloat()
        }

        fun setDist(dist: Float) {
            val angle = Math.toRadians(angleRadial.toDouble())
            super.x = (Math.cos(angle) * dist + widthScreen / 2.0).toFloat()
            super.y = (heightScreen / 2.0 - Math.sin(angle) * dist).toFloat()
        }

        companion object {

            fun regulateAngle(value: Float): Float {
                var range_val = value
                while (range_val < 0) range_val += 360
                while (range_val >= 360) range_val -= 360
                return range_val
            }
        }
    }

    class Percent(x: Float, y: Float) : Position(x / 100.0f * widthScreen, y / 100.0f * heightScreen) {

        override var x: Float
            get() = super.x
            set(x) {
                super.x = x / 100.0f * widthScreen
            }

        override var y: Float
            get() = super.y
            set(y) {
                super.y = y / 100.0f * heightScreen
            }
    }

    companion object {

        /* STATIC */

        var widthScreen = Gdx.graphics.width.toFloat()
            protected set
        var heightScreen = Gdx.graphics.height.toFloat()
            protected set

        val center: Position
            get() = Position(widthScreen / 2.0f, heightScreen / 2.0f)
    }

}
