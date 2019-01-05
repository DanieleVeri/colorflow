package com.colorflow.entity.trigger

import com.colorflow.entity.Entity
import com.colorflow.play.PlayStage

abstract class Trigger(protected var playStage: PlayStage) {

    abstract fun run(entity: Entity)

    companion object Factory {

        fun getDot(playStage: PlayStage): Trigger {
            return DotTrigger(playStage)
        }

        fun getBonus(playStage: PlayStage): Trigger {
            return BonusTrigger(playStage)
        }
    }
}
