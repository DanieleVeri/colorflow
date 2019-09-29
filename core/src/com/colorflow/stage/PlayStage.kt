package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.utils.viewport.Viewport
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
import com.colorflow.music.BeatSample
import com.colorflow.music.IEventListener
import kotlinx.coroutines.delay

class PlayStage(viewport: Viewport,
                protected val state: GameState,
                protected val assets: AssetProvider) : EffectStage(viewport), IEventListener {

    protected val dot_pool = DotPool(assets)
    protected val bonus_pool = BonusPool(assets)

    protected val spawner: EntitySpawner = EntitySpawner(dot_pool, bonus_pool)
    protected var ring: Ring = Ring(assets, state.ring_list.find { it.used }!!.src)

    protected var delta_alpha = 1.0f
    protected var confidence_threshold = .10f

    fun update() {
        actors.filter { it is Entity }.forEach { (it as Entity).destroy {} }
        clear()
        spawner.reset()
        shockwave(Position.center)
        ring = Ring(assets, state.ring_list.find { it.used }!!.src)
        addActor(ring)
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
        val collisions = actors.filter {
            it is Entity && Intersector.overlaps(it.bounds, ring.circle) }
        collisions.forEach {
            if(it is Bonus) {
                val bonus = it as Bonus
                bonus.destroy {
                    explosion(Color.WHITE, bonus.path.pos)
                    when (bonus.type) {
                        Bonus.Type.BOMB -> {
                            state.current_game!!.score.points += 400
                            shockwave(Position.center)
                            actors.forEach { dot ->
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
        Gdx.app.debug(this::class.java.simpleName, sample.bpm.toString())
        set_bg_color(Color(sample.confidence, sample.confidence, sample.confidence, 1f))
        spawner.dyn_velocity *= 3f
        ring.setScale(1.1f)
        delay(100)
        ring.setScale(1f)
        spawner.dyn_velocity /= 3f
    }

    override fun on_completition() {
        shockwave(Position.center)
        actors.forEach { dot ->
            if(dot is Dot ) {
                dot.addAction(Actions.removeActor())
                explosion(dot.colour.rgb, dot.path.pos)
            }
        }
        val action = RunnableAction()
        action.runnable = Runnable { state.current_game!!.gameover = true }
        addAction(Actions.delay(1f, action))
    }

    override fun dispose() {
        dot_pool.clear()
        bonus_pool.clear()
        super.dispose()
    }

}
