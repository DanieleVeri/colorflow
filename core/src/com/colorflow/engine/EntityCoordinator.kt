package com.colorflow.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.scenes.scene2d.Group
import com.colorflow.engine.entity.Entity
import com.colorflow.engine.entity.IMotionCoordinator
import com.colorflow.engine.entity.bonus.Bonus
import com.colorflow.engine.entity.bonus.BonusPool
import com.colorflow.graphic.Color
import com.colorflow.engine.entity.dot.Dot
import com.colorflow.engine.entity.dot.DotPool
import com.colorflow.graphic.Position
import kotlin.collections.ArrayList

class EntityCoordinator(protected val dot_pool: DotPool,
                        protected val bonus_pool: BonusPool): Group(), IMotionCoordinator {

    companion object {
        const val MAX_WAVE_RATE = 0.3f
        const val PARAM_UPDATE_RATE = 4f
        val SPAWN_DIST = (Math.sqrt(Math.pow(Position.heightScreen.toDouble(), 2.0) + Math.pow(Position.widthScreen.toDouble(), 2.0)) / 2).toFloat()
    }

    protected var wave_rate = 3f
    protected var wave_dots = 1f
    protected var last_wave = 0f
    protected var last_param_updated = 0f
    protected var time = 0f

    override var dot_velocity = 3.5f
    override var bonus_velocity = 1f
    override var path_type = IMotionCoordinator.PathType.RADIAL

    var bomb_chance = 0.1f
    var gold_chance = 0.1f

    override fun act(delta: Float) {
        time += delta
        if(time - last_param_updated > PARAM_UPDATE_RATE) {
            last_param_updated = time
            if(wave_dots < 6) wave_dots += 0.2f
            if(wave_rate > MAX_WAVE_RATE) wave_rate -= 0.1f
        }
        if(time - last_wave > wave_rate) {
            last_wave = time
            val list = ArrayList<Entity>()
            list.addAll(wave_dot_mix(wave_dots.toInt()))
            if(Math.random() < bomb_chance) list.add(bomb())
            if(Math.random() < gold_chance) list.add(gold())
            list.forEach {
                it.coordinator = this
                addActor(it)
            }
        }
        super.act(delta)
    }

    fun __debug_values() {
        Gdx.app.debug(this::class.java.simpleName, "\n time: $time \n " +
                "wave_dots: $wave_dots \n wave_rate: $wave_rate \n")
    }

    protected fun wave_dot_mix(num: Int): List<Dot> {
        val list = ArrayList<Dot>()
        val picked_colors = ArrayList<Color>()
        val num_constrained = if(num > 6) 6 else if (num < 1) 1 else num
        val start_pos = Position.Radial(0f, SPAWN_DIST)
        val start_ang = Math.random().toFloat() * 360f

        for (i in 0 until num_constrained) {
            picked_colors.add(Color.getRandomExcept(picked_colors))
            if (i == 0) {
                start_pos.set_angle(start_ang)
            } else {
                start_pos.set_angle(Position.Radial.regulate_angle(start_ang + Color.getAngleBetween(
                        picked_colors[picked_colors.size - 1],
                        picked_colors[0])))
            }
            if (Math.random() < 0.5) {
                list.add(dot_pool.get(Dot.Type.STD, picked_colors[picked_colors.size - 1], start_pos))
            } else {
                list.add(dot_pool.get(Dot.Type.REVERSE,
                        Color.getRandomExcept(picked_colors.subList(picked_colors.size - 1, picked_colors.size)), start_pos))
            }
        }
        return list
    }

    protected fun bomb(): Bonus {
        return bonus_pool.get(Bonus.Type.BOMB, Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST))
    }

    protected fun gold(): Bonus {
        return bonus_pool.get(Bonus.Type.GOLD, Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST))
    }
}
