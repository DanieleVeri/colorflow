package com.colorflow.entity.trigger

import com.badlogic.gdx.Gdx
import com.colorflow.entity.Entity
import com.colorflow.entity.dot.Dot
import com.colorflow.play.PlayStage
import com.colorflow.screen.PlayScreen
import com.colorflow.utility.Position

internal class DotTrigger(playStage: PlayStage) : Trigger(playStage) {

    override fun run(entity: Entity) {
        val d = entity as Dot
        val ring = playStage.ring
        val p = Position.Pixel(0f, 0f)
        p.x = d.x + d.width / 2.0f
        p.y = d.y + d.height / 2.0f
        when (d.type) {
            Dot.Type.STD -> if (ring!!.getColorFor(p.angleRadial) == d.colour) {
                playStage.playScreen.score.incPoints(10)
            } else {
                Gdx.input.vibrate(200)
                playStage.playScreen.state = PlayScreen.State.OVER
            }
            Dot.Type.REVERSE -> if (ring!!.getColorFor(p.angleRadial) == d.colour) {
                Gdx.input.vibrate(200)
                playStage.playScreen.state = PlayScreen.State.OVER
            } else {
                playStage.playScreen.score.incPoints(10)
            }
            Dot.Type.COIN -> if (ring!!.getColorFor(p.angleRadial) == d.colour) {
                playStage.playScreen.score.incCoins(1)
                playStage.playScreen.score.incPoints(10)
            } else {
                Gdx.input.vibrate(200)
                playStage.playScreen.state = PlayScreen.State.OVER
            }
        }
    }
}
