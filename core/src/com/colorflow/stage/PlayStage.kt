package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.play.BackgroundManager
import com.colorflow.play.EntitySpawner
import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.play.ring.Ring
import com.colorflow.AssetProvider
import com.colorflow.state.GameState
import com.colorflow.graphic.Position
import com.colorflow.graphic.effects.ExplosionPool
import com.colorflow.graphic.effects.ShockWave
import com.colorflow.music.BeatSample
import com.colorflow.music.IEventListener
import kotlinx.coroutines.delay

class PlayStage(viewport: Viewport,
                protected val state: GameState,
                protected val assets: AssetProvider) : Stage(viewport), IEventListener {

    protected val dot_pool = DotPool(assets)
    protected val bonus_pool = BonusPool(assets)
    protected val explosion_pool = ExplosionPool()
    protected val shockwave_layer = ShockWave()
    protected val spawner: EntitySpawner = EntitySpawner(dot_pool, bonus_pool)
    protected val background: BackgroundManager = BackgroundManager()
    protected var ring: Ring = Ring(assets, state.ring_list.find { it.used }!!.src)

    protected var delta_alpha = 1.0f
    protected var confidence_threshold = .10f

    fun reset() {
        shockwave_layer.children.filter { it is Entity }.forEach { (it as Entity).destroy {} }
        shockwave_layer.clear()
        spawner.reset()
        background.reset()
        ring = Ring(assets, state.ring_list.find { it.used }!!.src)
        clear()
        super.addActor(shockwave_layer)
        addActor(background)
        addActor(ring)
    }

    override fun addActor(actor: Actor?) {
        shockwave_layer.addActor(actor)
    }

    override fun act(delta: Float) {
        handle_collisions()
        spawner.update_time(delta)
        spawner.spawn().forEach { addActor(it) }
        super.act(delta * delta_alpha)
    }

    fun get_ring_listener(): InputProcessor {
        return ring.getListener()
    }

    protected fun handle_collisions() {
        val collisions = shockwave_layer.children.filter {
            it is Entity && Intersector.overlaps(it.bounds, ring.circle) }
        collisions.forEach {
            if(it is Bonus) {
                val bonus = it as Bonus
                bonus.destroy {
                    explosion(Color.WHITE, bonus.path.pos)
                    when (bonus.type) {
                        Bonus.Type.BOMB -> {
                            state.current_game!!.score.points += 400
                            shockwave_layer.start(Position.center.x, Position.center.y)
                            shockwave_layer.children.forEach { dot ->
                                if(dot is Dot ) {
                                    dot.addAction(Actions.removeActor())
                                    explosion(dot.colour.rgb, dot.path.pos)
                                }
                            }
                        }
                        Bonus.Type.GOLD -> {
                            state.current_game!!.score.coins += 10
                        }
                        else -> throw IllegalStateException()
                    }
                }
            } else if (it is Dot) {
                val dot = it as Dot
                dot.destroy {
                    val p = Position.Pixel(0f, 0f)
                    p.x = dot.x + dot.width / 2.0f
                    p.y = dot.y + dot.height / 2.0f
                    explosion(dot.colour.rgb, dot.path.pos)
                    when (dot.type) {
                        Dot.Type.STD -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                            state.current_game!!.score.points += 10
                        } else {
                            Gdx.input.vibrate(200)
                            state.current_game!!.gameover = true
                        }
                        Dot.Type.REVERSE -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                            Gdx.input.vibrate(200)
                            state.current_game!!.gameover = true
                        } else {
                            state.current_game!!.score.points += 10
                        }
                    }
                }
            }
        }
    }

    override suspend fun on_beat(sample: BeatSample) {
        if (sample.confidence < confidence_threshold) return
        spawner.dyn_velocity *= 3f
        ring.setScale(1.1f)
        delay(100)
        ring.setScale(1f)
        spawner.dyn_velocity /= 3f
    }

    protected fun explosion(color: Color, position: Position) {
        val obj = explosion_pool.get()
        addActor(obj)
        obj.start(color, position)
    }

    override fun dispose() {
        dot_pool.clear()
        bonus_pool.clear()
        explosion_pool.clear()
        super.dispose()
    }

}
