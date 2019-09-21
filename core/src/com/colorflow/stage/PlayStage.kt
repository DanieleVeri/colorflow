package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.persistence.IStorage
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
import com.colorflow.utils.AssetProvider
import com.colorflow.utils.Position
import com.colorflow.utils.effects.ExplosionPool
import com.colorflow.utils.effects.ShockWave
import kotlinx.coroutines.delay

class PlayStage(viewport: Viewport,
                private val _assets: AssetProvider,
                private val _persistence: IStorage,
                private val _score: Score,
                private val _play_screen: PlayScreen) : Stage(viewport) {

    private val _dot_pool = DotPool(_assets)
    private val _bonus_pool = BonusPool(_assets)
    private val _explosion_pool = ExplosionPool()
    private val _shockwave_layer = ShockWave()
    private val _spawner: EntitySpawner = EntitySpawner(_dot_pool, _bonus_pool)
    private val _background: BackgroundManager = BackgroundManager()
    private lateinit var _ring: Ring

    private var _delta_alpha = 1.0f

    fun reset() {
        _shockwave_layer.children.filter { it is Entity }.forEach { (it as Entity).destroy {  } }
        _shockwave_layer.clear()
        _spawner.reset()
        _background.reset()

        clear()
        super.addActor(_shockwave_layer)

        addActor(_background)
        _ring = Ring(_assets, _persistence.used_ring.src)
        addActor(_ring)
    }

    override fun addActor(actor: Actor?) {
        _shockwave_layer.addActor(actor)
    }

    override fun act(delta: Float) {
        if (_play_screen.state !== PlayScreen.State.PLAY)
            return
        _handle_collisions()
        _spawner.update_time(delta)
        _spawner.spawn().forEach { addActor(it) }
        super.act(delta * _delta_alpha)
    }

    override fun dispose() {
        super.dispose()
        _dot_pool.clear()
        _bonus_pool.clear()
        _explosion_pool.clear()
    }

    fun get_ring_listener(): InputProcessor {
        return _ring.getListener()
    }

    suspend fun on_beat(confidence: Float) {
         _spawner.dyn_velocity *= 3f
        _ring.setScale(1.1f)
        delay(100)
        _ring.setScale(1f)
        _spawner.dyn_velocity /= 3f
    }

    private fun _handle_collisions() {
        val collisions = _shockwave_layer.children.filter { it is Entity }.map { it as Entity }
            .filter { Intersector.overlaps(it.bounds, this._ring.circle) }

        // Bonus
        collisions.filter { it is Bonus }.map { it as Bonus }.map { bonus ->
            bonus.destroy {
                _explosion_pool.start(this, Color.WHITE, bonus.path.pos)
                when (bonus.type) {
                    Bonus.Type.BOMB -> {
                        _shockwave_layer.start(Position.center.x, Position.center.y)
                        _shockwave_layer.children.filter { it is Dot }.map { it as Dot }.map { dot ->
                            dot.addAction(Actions.removeActor())
                            _explosion_pool.start(this, dot.colour.rgb, dot.path.pos)
                        }
                        _score.incPoints(400)
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
                _explosion_pool.start(this, dot.colour.rgb, dot.path.pos)
                when (dot.type) {
                    Dot.Type.STD -> if (_ring.getColorFor(p.angleRadial) == dot.colour) {
                        _score.incPoints(10)
                    } else {
                        Gdx.input.vibrate(200)
                        //_play_screen.state = PlayScreen.State.OVER
                    }
                    Dot.Type.REVERSE -> if (_ring.getColorFor(p.angleRadial) == dot.colour) {
                        Gdx.input.vibrate(200)
                       // _play_screen.state = PlayScreen.State.OVER
                    } else {
                        _score.incPoints(10)
                    }
                    Dot.Type.COIN -> if (_ring.getColorFor(p.angleRadial) == dot.colour) {
                        _score.incCoins(1)
                        _score.incPoints(10)
                    } else {
                        Gdx.input.vibrate(200)
                        //_play_screen.state = PlayScreen.State.OVER
                    }
                }
            }
        }
    }

}
