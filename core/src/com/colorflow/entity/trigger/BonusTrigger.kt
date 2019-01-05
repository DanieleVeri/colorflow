package com.colorflow.entity.trigger

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.colorflow.entity.Entity
import com.colorflow.entity.bonus.Bonus
import com.colorflow.entity.dot.Dot
import com.colorflow.play.PlayStage
import com.colorflow.utility.effect.ExplosionPool

class BonusTrigger(playStage: PlayStage) : Trigger(playStage) {

    override fun run(entity: Entity) {
        val b = entity as Bonus
        when (b.type) {
            Bonus.Type.BOMB -> {
                val list = playStage.actors
                for (i in 0 until list.size) {
                    if (list.get(i) is Dot) {
                        val d = list.get(i) as Dot
                        d.addAction(Actions.removeActor())
                        ExplosionPool.getInstance().start(playStage, d.colour.rgb, d.position)
                    }
                }
                playStage.playScreen.score.incPoints(400)
            }
            else -> throw IllegalStateException()
        }

    }
}
