import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils

/** Draws an arc with 'stroke' of given width  */
fun ShapeRenderer.strokeArc(strokeWidth: Float, x: Float, y: Float, radius: Float, start: Float, degrees: Float, sampling: Float = 2f, color: Color) {
    val segments = ((6 * Math.cbrt(radius.toDouble()) * (Math.abs(degrees) / 360.0f)) * sampling).toInt()
    val colorBits = color.toFloatBits()

    for (i in 0 until segments) {
        val x1 = radius * MathUtils.cosDeg(start + (degrees / segments) * i)
        val y1 = radius * MathUtils.sinDeg(start + (degrees / segments) * i)

        val x2 = (radius - strokeWidth) * MathUtils.cosDeg(start + (degrees / segments) * i)
        val y2 = (radius - strokeWidth) * MathUtils.sinDeg(start + (degrees / segments) * i)

        val x3 = radius * MathUtils.cosDeg(start + (degrees / segments) * (i + 1))
        val y3 = radius * MathUtils.sinDeg(start + (degrees / segments) * (i + 1))

        val x4 = (radius - strokeWidth) * MathUtils.cosDeg(start + (degrees / segments) * (i + 1))
        val y4 = (radius - strokeWidth) * MathUtils.sinDeg(start + (degrees / segments) * (i + 1))

        renderer.color(colorBits)
        renderer.vertex(x + x1, y + y1, 0f)
        renderer.color(colorBits)
        renderer.vertex(x + x3, y + y3, 0f)
        renderer.color(colorBits)
        renderer.vertex(x + x2, y + y2, 0f)

        renderer.color(colorBits)
        renderer.vertex(x + x3, y + y3, 0f)
        renderer.color(colorBits)
        renderer.vertex(x + x2, y + y2, 0f)
        renderer.color(colorBits)
        renderer.vertex(x + x4, y + y4, 0f)
    }
}