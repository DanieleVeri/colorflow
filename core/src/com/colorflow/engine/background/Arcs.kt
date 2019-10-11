package com.colorflow.engine.background

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.utils.Disposable
import com.colorflow.graphic.Position
import strokeArc

class Arcs: Actor(), Disposable {
    protected val shape = ShapeRenderer()
    protected val rot_ang = arrayOf(0f, 0f, 0f)

    val rot_vel = arrayOf(0.5f, 0.4f, 0.6f)
    val colors = arrayOf(Color.BLUE, Color.GREEN, Color.RED)
    var radius_offset = MAX_VISIBLE
    var arc_width: Float = 0f

    override fun act(delta: Float) {
        for (i in 0 until rot_ang.size) {
            rot_ang[i] += rot_vel[i]
            if (rot_ang[i] >= 360f) rot_ang[i] -= 360f
            if (rot_ang[i] < 0f) rot_ang[i] += 360f
        }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.end()
        Gdx.graphics.gL20.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shape.begin(ShapeRenderer.ShapeType.Line)
        shape.setColor(1.0f, 1.0f, 1.0f, 0.2f)
        shape.circle(Position.center.x, Position.center.y, arc_width*2)
        shape.circle(Position.center.x, Position.center.y, arc_width*3)
        shape.circle(Position.center.x, Position.center.y, arc_width*4)
        shape.end()
        shape.begin(ShapeRenderer.ShapeType.Filled)

        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*4+radius_offset,
                rot_ang[0], 60f, 2f, colors[0])
        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*4+radius_offset,
                120f+rot_ang[0], 60f, 2f, colors[0])
        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*4+radius_offset,
                240f+rot_ang[0], 60f, 2f, colors[0])

        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*3+radius_offset,
                rot_ang[1], 60f, 2f, colors[1])
        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*3+radius_offset,
                120f+rot_ang[1], 60f, 2f, colors[1])
        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*3+radius_offset,
                240f+rot_ang[1], 60f, 2f, colors[1])

        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*2+radius_offset,
                rot_ang[2], 60f, 2f, colors[2])
        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*2+radius_offset,
                120f+rot_ang[2], 60f, 2f, colors[2])
        shape.strokeArc(arc_width, Position.center.x, Position.center.y, arc_width*2+radius_offset,
                240f+rot_ang[2], 60f, 2f, colors[2])

        shape.end()
        Gdx.graphics.gL20.glDisable(GL20.GL_BLEND)
        batch?.begin()
    }

    override fun dispose() {
        shape.dispose()
    }

    fun fadein() {
        radius_offset = MAX_VISIBLE
        val action = RunnableAction()
        action.runnable = Runnable {
            if(radius_offset <= 0)
                return@Runnable
            radius_offset -= 3f
            addAction(Actions.delay(0.03f, action))
        }
        addAction(Actions.delay(0.03f, action))
    }

    fun fadeout() {
        val action = RunnableAction()
        action.runnable = Runnable {
            if(radius_offset > MAX_VISIBLE)
                return@Runnable
            radius_offset += 3f
            addAction(Actions.delay(0.03f, action))
        }
        addAction(Actions.delay(0.03f, action))
    }

    companion object {
        val MAX_VISIBLE = (Math.sqrt(Math.pow(Position.heightScreen.toDouble(), 2.0) +
                Math.pow(Position.widthScreen.toDouble(), 2.0)) / 2).toFloat()
    }
}