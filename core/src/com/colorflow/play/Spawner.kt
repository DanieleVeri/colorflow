package com.colorflow.play

import com.colorflow.play.entity.Path
import com.colorflow.play.entity.bonus.Bonus
import com.colorflow.play.entity.bonus.BonusPool
import com.colorflow.utils.Color
import com.colorflow.play.entity.dot.Dot
import com.colorflow.play.entity.dot.DotPool
import com.colorflow.stage.PlayStage
import com.colorflow.utils.Position

import java.util.ArrayList

class Spawner(private val playStage: PlayStage) {

    companion object {
        private val SPAWN_DIST = (Math.sqrt(Math.pow(Position.heightScreen.toDouble(), 2.0) + Math.pow(Position.widthScreen.toDouble(), 2.0)) / 2).toFloat()
    }

    private var dotSpeed: Float = 0.toFloat()
    private var bonusSpeed: Float = 0.toFloat()
    private var dotPath: Path.Type? = null
    private var timer: Float = 0f
    private val pickedColors: MutableList<Color>
    private val start: Position.Radial

    init {
        this.pickedColors = ArrayList()
        this.start = Position.Radial(0f, SPAWN_DIST)
        reset()
    }

    fun reset() {
        dotSpeed = 2.5f
        bonusSpeed = 1f
        dotPath = Path.Type.RADIAL
        timer = 0f
    }

    fun setDotSpeed(dotSpeed: Float) {
        this.dotSpeed = dotSpeed
    }

    fun setBonusSpeed(bonusSpeed: Float) {
        this.bonusSpeed = bonusSpeed
    }

    fun setDotPath(dotPath: Path.Type) {
        this.dotPath = dotPath
    }

    fun act(delta: Float)
    {
        timer += delta
        // Dots
        if (timer > 2) {
            timer = 0f
            waveDotMix(4)
        }
        // Bonus
        if (Math.random() < 0.0005) {
            bonus()
        }
    }

    private fun waveDotStd(num: Int) {
        pickedColors.clear()
        val ang = Math.random().toFloat() * 360f
        if (num > 6 || num <= 0) {
            throw IndexOutOfBoundsException()
        }
        for (i in 0 until num) {
            pickedColors.add(Color.getRandomExcept(pickedColors))
            if (i == 0) {
                start.set_angle(ang)
            } else {
                start.set_angle(Position.Radial.regulate_angle(ang + Color.getAngleBetween(
                        pickedColors[pickedColors.size - 1],
                        pickedColors[0])))
            }
            playStage.addActor(DotPool.instance.get(Dot.Type.COIN, pickedColors[pickedColors.size - 1],
                    dotPath!!, start, dotSpeed))
        }
    }

    private fun waveDotMix(num: Int) {
        pickedColors.clear()
        val ang = Math.random().toFloat() * 360f
        if (num > 6 || num <= 0) {
            throw IndexOutOfBoundsException()
        }
        for (i in 0 until num) {
            pickedColors.add(Color.getRandomExcept(pickedColors))
            if (i == 0) {
                start.set_angle(ang)
            } else {
                start.set_angle(Position.Radial.regulate_angle(ang + Color.getAngleBetween(
                        pickedColors[pickedColors.size - 1],
                        pickedColors[0])))
            }
            if (Math.random() < 0.5) {
                playStage.addActor(DotPool.instance.get(Dot.Type.STD, pickedColors[pickedColors.size - 1],
                        dotPath!!, start, dotSpeed))
            } else {
                playStage.addActor(DotPool.instance.get(Dot.Type.REVERSE,
                        Color.getRandomExcept(pickedColors.subList(pickedColors.size - 1, pickedColors.size)),
                        dotPath!!, start, dotSpeed))
            }
        }
    }

    private fun bonus() {
        playStage.addActor(BonusPool.instance.get(Bonus.Type.BOMB, Path.Type.RADIAL,
                Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST), bonusSpeed))
    }

}
