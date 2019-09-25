package com.colorflow.utils

enum class Color {
    RED, YELLOW, GREEN, MAGENTA, BLUE, CYAN;

    val rgb: com.badlogic.gdx.graphics.Color
        get() {
            return when (this) {
                RED -> com.badlogic.gdx.graphics.Color.RED
                YELLOW -> com.badlogic.gdx.graphics.Color.YELLOW
                GREEN -> com.badlogic.gdx.graphics.Color.GREEN
                MAGENTA -> com.badlogic.gdx.graphics.Color.MAGENTA
                BLUE -> com.badlogic.gdx.graphics.Color.BLUE
                CYAN -> com.badlogic.gdx.graphics.Color.CYAN
            }
        }

    companion object {

        fun getRandomExcept(colors: List<Color>): Color {
            var colorNum: Int
            while (true) {
                colorNum = (Math.random() * 6.0).toInt()
                when (colorNum) {
                    0 -> if (!colors.contains(RED))
                        return RED
                    1 -> if (!colors.contains(YELLOW))
                        return YELLOW
                    2 -> if (!colors.contains(GREEN))
                        return GREEN
                    3 -> if (!colors.contains(MAGENTA))
                        return MAGENTA
                    4 -> if (!colors.contains(BLUE))
                        return BLUE
                    5 -> if (!colors.contains(CYAN))
                        return CYAN
                    else -> throw IllegalStateException()
                }
            }
        }

        fun getAngleBetween(c1: Color, c2: Color): Float {
            val dif = c2.ordinal - c1.ordinal
            return (60 * dif).toFloat()
        }
    }
}
