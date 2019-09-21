package com.colorflow.play

import com.colorflow.play.entity.Entity
import com.colorflow.play.entity.Path
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.utils.Color
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.utils.Position
import kotlin.collections.ArrayList

class EntitySpawner(private val _dot_pool: DotPool,
        private val _bonus_pool: BonusPool) {

    var dyn_velocity = 1.5f
    var dot_speed: Float = 1.5f
    var bonus_speed: Float = 1f
    var dot_path: Path.Type = Path.Type.RADIAL_DYNAMIC
    var bonus_chance: Float = 0.1f
    var delta_time_spawn: Float = 2f

    private var _timer: Float = 0f

    fun reset() {
        dot_speed = 1.5f
        dyn_velocity = 1.5f
        bonus_speed = 1f
        bonus_chance = 0.1f
        dot_path = Path.Type.RADIAL_DYNAMIC
        delta_time_spawn = 2f
        _timer = 0f
    }

    fun update_time(delta: Float) {
        _timer += delta
    }

    fun spawn(): List<Entity>
    {
        val list = ArrayList<Entity>()
        if(_timer > delta_time_spawn) {
            _timer = 0f
            list.addAll(_wave_dot_mix(6))
            if (Math.random() < bonus_chance)
                list.add(_bonus())
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
                list.add(_dot_pool.get(Dot.Type.STD, picked_colors[picked_colors.size - 1],
                        dot_path, start_pos, dot_speed))
            } else {
                list.add(_dot_pool.get(Dot.Type.REVERSE,
                        Color.getRandomExcept(picked_colors.subList(picked_colors.size - 1, picked_colors.size)),
                        dot_path, start_pos, dot_speed))
            }
        }
        return list
    }

    private fun _bonus(): Bonus {
        return _bonus_pool.get(Bonus.Type.BOMB, Path.Type.RADIAL_STATIC,
                Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST), bonus_speed)
    }

    companion object {
        val SPAWN_DIST = (Math.sqrt(
                Math.pow(Position.heightScreen.toDouble(), 2.0) +
                        Math.pow(Position.widthScreen.toDouble(), 2.0)) / 2).toFloat()
    }
}
