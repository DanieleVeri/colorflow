package com.colorflow.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.colorflow.engine.EntityCoordinator
import com.colorflow.engine.entity.Entity
import com.colorflow.engine.entity.bonus.Bonus
import com.colorflow.engine.entity.bonus.BonusPool
import com.colorflow.engine.entity.dot.Dot
import com.colorflow.engine.entity.dot.DotPool
import com.colorflow.engine.ring.Ring
import com.colorflow.AssetProvider
import com.colorflow.graphic.effects.EffectStage
import com.colorflow.state.GameState
import com.colorflow.graphic.Position
import com.colorflow.graphic.laction
import com.colorflow.music.BeatSample
import com.colorflow.music.IEventListener
import com.colorflow.music.Music
import java.util.*
import java.util.Collections.min
import kotlin.math.floor

class PlayStage(viewport: Viewport,
                protected val state: GameState,
                protected val assets: AssetProvider,
                protected val music: Music) : EffectStage(viewport), IEventListener {

    protected val dot_pool = DotPool(assets)
    protected val bonus_pool = BonusPool(assets)

    protected lateinit var background_color: Color

    protected lateinit var coordinator: EntityCoordinator
    protected lateinit var ring: Ring
    fun get_ring_listener() = ring.getListener()

    fun reset() {
        Gdx.app.debug(this::class.java.simpleName, "reset")
        /* clean */
        dot_pool.destroy_all()
        bonus_pool.destroy_all()
        clear()
        /* lightweight recreation */
        coordinator = EntityCoordinator(dot_pool, bonus_pool)
        ring = Ring(assets, state.ring_list.find { it.used }!!.src)
        /* add actors */
        addActor(music)
        addActor(coordinator)
        addActor(ring)
        addAction(Actions.forever(laction { handle_collisions() }))
        /* effects */
        effect_layer.stop_all()
        effect_layer.spectrum { s -> s.setUniformi("iChannel0", 1) }
        effect_layer.twinkling()
    }

    override fun dispose() {
        dot_pool.clear()
        bonus_pool.clear()
        super.dispose()
    }

    protected fun game_over() {
        state.current_game!!.gameover = true
        dot_pool.destroy_all()
        bonus_pool.destroy_all()
    }

    protected fun handle_collisions() {
        val collisions = coordinator.children.filter {
            it is Entity && Intersector.overlaps(it.bounds, ring.circle) }
        collisions.forEach {
            if(it is Bonus) {
                val bonus = it as Bonus
                bonus.destroy {
                    effect_layer.explosion(Color.WHITE, bonus.position)
                    when (bonus.type) {
                        Bonus.Type.BOMB -> {
                            state.current_game!!.score.points += 400
                            effect_layer.shockwave(Position.center)
                            effect_layer.glow(Position.center)
                            dot_pool.destroy_all { dot ->
                                effect_layer.explosion(dot.colour.rgb, dot.position)
                            }
                        }
                        Bonus.Type.GOLD -> {
                            assets.get_sound("coins").play(1f)
                            state.current_game!!.score.coins += 10
                        }
                        Bonus.Type.TODO -> {

                        }
                    }
                }
            } else if (it is Dot) {
                val dot = it as Dot
                dot.destroy {
                    val p = Position.Pixel(0f, 0f)
                    p.x = dot.x + dot.width / 2.0f
                    p.y = dot.y + dot.height / 2.0f
                    effect_layer.explosion(dot.colour.rgb, dot.position)
                    when (dot.type) {
                        Dot.Type.STD -> if (ring.getColorFor(p.angleRadial) != dot.colour) game_over()
                            else state.current_game!!.score.points += 10
                        Dot.Type.REVERSE -> if (ring.getColorFor(p.angleRadial) == dot.colour) game_over()
                            else state.current_game!!.score.points += 10
                    }
                }
            }
        }
    }

    override fun on_beat(music: Music, sample: BeatSample) {
        // Gdx.app.debug(this::class.java.simpleName, sample.bpm.toString())
        background_color = Color(sample.confidence, sample.confidence, sample.confidence, 1f)
        coordinator.dot_velocity *= 3f
        ring.setScale(1.1f)
        music.addAction(Actions.sequence(Actions.delay(0.1f), laction {
            ring.setScale(1f)
            coordinator.dot_velocity /= 3f
        }))
    }

    private val SIZE = 1024
    private val pixmap = Pixmap(SIZE, 1, Pixmap.Format.RGBA8888)
    private val text_fft = Texture(pixmap)

    override fun on_fft(music: Music, buffer: FloatArray) {
        val color = Color()
        for (i in 0 until SIZE) {
            color.set(buffer[i], 0f, 0f, 1f)
            pixmap.drawPixel(i, 0, Color.rgba8888(color))
        }
        text_fft.draw(pixmap, 0, 0)
        Gdx.graphics.gL20.glActiveTexture(GL20.GL_TEXTURE1)
        text_fft.bind()
        Gdx.graphics.gL20.glActiveTexture(GL20.GL_TEXTURE0)
    }

    override fun on_completition() {}
}
