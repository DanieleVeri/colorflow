package com.colorflow.play

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.play.entity.dot.Color
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.play.entity.trigger.Trigger
import com.colorflow.play.ring.Ring
import com.colorflow.screen.PlayScreen
import com.colorflow.utility.effects.Explosion
import com.colorflow.utility.effects.ExplosionPool
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayStage(viewport: Viewport, val playScreen: PlayScreen) : Stage(viewport) {
    var ring: Ring? = null
        set(ring) {
            field = ring
            addActor(ring)
        }
    private val spawner: Spawner = Spawner(this)
    private val bgManager: BGManager = BGManager(this)

    private var isPlaying = true
    private var timer = 0f

    fun setState(state: PlayScreen.State) {
        isPlaying = state == PlayScreen.State.PLAY
    }

    fun reset() {
        for (a in actors) {
            if (a is Dot) {
                DotPool.instance.free(a)
            }
            if (a is Bonus) {
                BonusPool.instance.free(a)
            }
            if (a is Explosion) {
                ExplosionPool.getInstance().free(a)
            }
        }
        if (this.ring != null) this.ring!!.dispose()
        clear()
        playScreen.game.music_manager.reset()
        ring = Ring(playScreen.game.persistence.usedRing)
        playScreen.game.music_manager.add_beat_cb {
            //bgManager.bgColor = Color.getRandomExcept(emptyList()).rgb
            this.ring!!.setScale(1.1f)
            GlobalScope.launch { delay(100); ring!!.setScale(1f) }
        }
        spawner.reset()
        bgManager.reset()
        isPlaying = true
    }

    override fun act(delta: Float) {
        if (!isPlaying) {
            return
        }
        detectCollision()
        spawn(delta)
        super.act(delta)
    }

    override fun draw() {
        bgManager.render()
        super.draw()
    }

    override fun dispose() {
        super.dispose()
        this.ring?.dispose()
        DotPool.instance.clear()
        BonusPool.instance.clear()
        ExplosionPool.getInstance().clear()
    }

    private fun spawn(delta: Float) {
        timer += delta
        /* Dots */
        if (timer > 2) {
            timer = 0f
            spawner.waveDotMix(4)
        }
        /* Bonus */
        if (Math.random() < 0.0005) {
            spawner.bonus()
        }
    }

    private fun detectCollision() {
        for (a in actors)
            if (a is Bonus)
                if (Intersector.overlaps(a.bounds, this.ring!!.circle))
                    a.destroy(Trigger.getBonus(this))

        for (a in actors)
            if (a is Dot)
                if (Intersector.overlaps(a.bounds, this.ring!!.circle))
                    a.destroy(Trigger.getDot(this))
    }

}
