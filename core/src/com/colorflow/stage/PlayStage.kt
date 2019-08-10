package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.music.IMusicAnalyzer
import com.colorflow.music.IMusicManager
import com.colorflow.persistence.IStorage
import com.colorflow.play.BGManager
import com.colorflow.play.Score
import com.colorflow.play.Spawner
import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.play.ring.Ring
import com.colorflow.screen.PlayScreen
import com.colorflow.utils.Position
import com.colorflow.utils.effects.Explosion
import com.colorflow.utils.effects.ExplosionPool
import com.colorflow.utils.effects.ShockWave
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayStage(viewport: Viewport,
                private val persistence: IStorage,
                private val score: Score,
                private val play_screen: PlayScreen,
                private val music_manager: IMusicManager,
                private val music_analyzer: IMusicAnalyzer) : Stage(viewport) {

    private val spawner: Spawner = Spawner(this)
    private val background: BGManager = BGManager()
    private var ring: Ring = Ring(persistence.used_ring.src)

    fun reset() {
        for (a in actors) {
            if (a is Dot)
                DotPool.instance.free(a)
            if (a is Bonus)
                BonusPool.instance.free(a)
            if (a is Explosion)
                ExplosionPool.instance.free(a)
        }
        this.ring.dispose()
        spawner.reset()
        background.reset()
        clear()///////
        super.addActor(ShockWave.getInstance())
        addActor(background)
        ring = Ring(persistence.used_ring.src)
        addActor(ring)
        music_analyzer.add_beat_cb {
            GlobalScope.launch {
                ring.setScale(1.1f)
                delay(100)
                ring.setScale(1f)
            }
        }
        //music_analyzer.stop_beat_flow()
        music_analyzer.start_beat_flow("0")
    }

    override fun addActor(actor: Actor?) {
        ShockWave.getInstance().addActor(actor)
    }

    override fun act(delta: Float) {
        if (play_screen.state !== PlayScreen.State.PLAY)
            return
        _handle_collisions()
        spawner.act(delta)
        super.act(delta)
    }

    override fun dispose() {
        super.dispose()
        this.ring.dispose()
        DotPool.instance.clear()
        BonusPool.instance.clear()
        ExplosionPool.instance.clear()
    }

    fun get_ring_listener(): InputProcessor {
        return ring.getListener()
    }

    private fun _handle_collisions() {
        val collisions = ShockWave.getInstance().children.filter { it is Entity }.map { it as Entity }
            .filter { Intersector.overlaps(it.bounds, this.ring.circle) }
        if(collisions.isNotEmpty())
            ShockWave.getInstance().start(Position.center.x, Position.center.y)

        // Bonus
        collisions.filter { it is Bonus }.map { it as Bonus }.map { bonus ->
            bonus.destroy {
                when (bonus.type) {
                    Bonus.Type.BOMB -> {
                        actors.filter { it is Dot }.map { it as Dot }.map { dot ->
                            dot.addAction(Actions.removeActor())
                            ExplosionPool.instance.start(this, dot.colour.rgb, dot.position)
                        }
                        score.incPoints(400)
                    }
                    else -> throw IllegalStateException()
                }
            }
        }

        // Dots
        collisions.filter { it is Dot }.map { it as Dot }.map { dot ->
            dot.destroy {
                val p = Position.Pixel(0f, 0f)
                p.x = dot.x + dot.width / 2.0f
                p.y = dot.y + dot.height / 2.0f
                when (dot.type) {
                    Dot.Type.STD -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                        score.incPoints(10)
                    } else {
                        Gdx.input.vibrate(200)
                        //play_screen.state = PlayScreen.State.OVER
                    }
                    Dot.Type.REVERSE -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                        Gdx.input.vibrate(200)
                        //play_screen.state = PlayScreen.State.OVER
                    } else {
                        score.incPoints(10)
                    }
                    Dot.Type.COIN -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                        score.incCoins(1)
                        score.incPoints(10)
                    } else {
                        Gdx.input.vibrate(200)
                        //play_screen.state = PlayScreen.State.OVER
                    }
                }
            }
        }
    }

}
