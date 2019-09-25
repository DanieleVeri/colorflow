package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.os.IStorage
import com.colorflow.play.BackgroundManager
import com.colorflow.play.Score
import com.colorflow.play.EntitySpawner
import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.play.ring.Ring
import com.colorflow.screen.PlayScreen
import com.colorflow.AssetProvider
import com.colorflow.GameState
import com.colorflow.utils.Position
import com.colorflow.effects.ExplosionPool
import com.colorflow.effects.ShockWave
import kotlinx.coroutines.delay

class PlayStage(viewport: Viewport,
                private val state: GameState,
                private val assets: AssetProvider) : Stage(viewport) {

    private val dot_pool = DotPool(assets)
    private val bonus_pool = BonusPool(assets)
    private val explosion_pool = ExplosionPool()
    private val shockwave_layer = ShockWave()
    private val spawner: EntitySpawner = EntitySpawner(dot_pool, bonus_pool)
    private val background: BackgroundManager = BackgroundManager()
    private var ring: Ring = Ring(assets, state.ring_list.find { it.used }!!.src)

    private var delta_alpha = 1.0f
    private var confidence_threshold = .10f

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
        _handle_collisions()
        spawner.update_time(delta)
        spawner.spawn().forEach { addActor(it) }
        super.act(delta * delta_alpha)
    }

    override fun dispose() {
        super.dispose()
        dot_pool.clear()
        bonus_pool.clear()
        explosion_pool.clear()
    }

    fun get_ring_listener(): InputProcessor {
        return ring.getListener()
    }

    suspend fun on_beat(confidence: Float) {
        if (confidence < confidence_threshold) return
        spawner.dyn_velocity *= 3f
        ring.setScale(1.1f)
        delay(100)
        ring.setScale(1f)
        spawner.dyn_velocity /= 3f
    }

    private fun _handle_collisions() {
        val collisions = shockwave_layer.children.filter { it is Entity }.map { it as Entity }
            .filter { Intersector.overlaps(it.bounds, this.ring.circle) }

        // Bonus
        collisions.filter { it is Bonus }.map { it as Bonus }.map { bonus ->
            bonus.destroy {
                explosion_pool.start(this, Color.WHITE, bonus.path.pos)
                when (bonus.type) {
                    Bonus.Type.BOMB -> {
                        shockwave_layer.start(Position.center.x, Position.center.y)
                        shockwave_layer.children.filter { it is Dot }.map { it as Dot }.map { dot ->
                            dot.addAction(Actions.removeActor())
                            explosion_pool.start(this, dot.colour.rgb, dot.path.pos)
                        }
                        state.current_game!!.score.incPoints(400)
                    }
                    Bonus.Type.GOLD -> {
                        state.current_game!!.score.incCoins(10)
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
                explosion_pool.start(this, dot.colour.rgb, dot.path.pos)
                when (dot.type) {
                    Dot.Type.STD -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                        state.current_game!!.score.incPoints(10)
                    } else {
                        Gdx.input.vibrate(200)
                        state.current_game!!.gameover = true
                    }
                    Dot.Type.REVERSE -> if (ring.getColorFor(p.angleRadial) == dot.colour) {
                        Gdx.input.vibrate(200)
                        state.current_game!!.gameover = true
                    } else {
                        state.current_game!!.score.incPoints(10)
                    }
                }
            }
        }
    }

}
