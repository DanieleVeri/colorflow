package com.colorflow.play

import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.Path
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.graphic.Color
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.graphic.Position
import kotlin.collections.ArrayList

class EntitySpawner(protected val dot_pool: DotPool,
                    protected val bonus_pool: BonusPool) {

    var dyn_velocity = 2.5f
    var dot_speed: Float = 2.5f
    var bonus_speed: Float = 1f
    var dot_path: Path.Type = Path.Type.RADIAL_DYNAMIC
    var bomb_chance: Float = 1.0f
    var gold_chance: Float = 0.1f
    var delta_time_spawn: Float = 2f
    protected var timer: Float = 0f

    fun update_time(delta: Float) {
        timer += delta
    }

    fun spawn(): List<Entity>
    {
        val list = ArrayList<Entity>()
        if(timer > delta_time_spawn) {
            timer = 0f
            list.addAll(_wave_dot_mix(6))
            if (Math.random() < bomb_chance)
                list.add(_bomb())
            if (Math.random() < gold_chance)
                list.add(_gold())
        }
        list.forEach { it.path.get_dyn_velocity = {dyn_velocity} }
        return list
    }

    private fun _wave_dot_mix(num: Int): List<Dot> {
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
                list.add(dot_pool.get(Dot.Type.STD, picked_colors[picked_colors.size - 1],
                        dot_path, start_pos, dot_speed))
            } else {
                list.add(dot_pool.get(Dot.Type.REVERSE,
                        Color.getRandomExcept(picked_colors.subList(picked_colors.size - 1, picked_colors.size)),
                        dot_path, start_pos, dot_speed))
            }
        }
        return list
    }

    private fun _bomb(): Bonus {
        return bonus_pool.get(Bonus.Type.BOMB, Path.Type.RADIAL_STATIC,
                Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST), bonus_speed)
    }

    private fun _gold(): Bonus {
        return bonus_pool.get(Bonus.Type.GOLD, Path.Type.RADIAL_STATIC,
                Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST), bonus_speed)
    }

    companion object {
        val SPAWN_DIST = (Math.sqrt(Math.pow(Position.heightScreen.toDouble(), 2.0) +
                Math.pow(Position.widthScreen.toDouble(), 2.0)) / 2).toFloat()
    }
}
