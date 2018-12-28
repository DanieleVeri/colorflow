package com.colorflow.play

import com.colorflow.entity.Path
import com.colorflow.entity.bonus.Bonus
import com.colorflow.entity.bonus.BonusPool
import com.colorflow.entity.dot.Color
import com.colorflow.entity.dot.Dot
import com.colorflow.entity.dot.DotPool
import com.colorflow.utility.Position

import java.util.ArrayList

class Spawner(private val playStage: PlayStage) {
    private var dotSpeed: Float = 0.toFloat()
    private var bonusSpeed: Float = 0.toFloat()
    private var dotPath: Path.Type? = null

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

    fun waveDotStd(num: Int) {
        pickedColors.clear()
        val ang = Math.random().toFloat() * 360f
        if (num > 6 || num <= 0) {
            throw IndexOutOfBoundsException()
        }
        for (i in 0 until num) {
            pickedColors.add(Color.Companion.getRandomExcept(pickedColors))
            if (i == 0) {
                start.setAngle(ang)
            } else {
                start.setAngle(Position.Radial.regulateAngle(ang + Color.Companion.getAngleBetween(
                        pickedColors[pickedColors.size - 1],
                        pickedColors[0])))
            }
            playStage.addActor(DotPool.instance.get(Dot.Type.COIN, pickedColors[pickedColors.size - 1],
                    dotPath!!, start, dotSpeed))
        }
    }

    fun waveDotMix(num: Int) {
        pickedColors.clear()
        val ang = Math.random().toFloat() * 360f
        if (num > 6 || num <= 0) {
            throw IndexOutOfBoundsException()
        }
        for (i in 0 until num) {
            pickedColors.add(Color.Companion.getRandomExcept(pickedColors))
            if (i == 0) {
                start.setAngle(ang)
            } else {
                start.setAngle(Position.Radial.regulateAngle(ang + Color.Companion.getAngleBetween(
                        pickedColors[pickedColors.size - 1],
                        pickedColors[0])))
            }
            if (Math.random() < 0.5) {
                playStage.addActor(DotPool.instance.get(Dot.Type.STD, pickedColors[pickedColors.size - 1],
                        dotPath!!, start, dotSpeed))
            } else {
                playStage.addActor(DotPool.instance.get(Dot.Type.REVERSE,
                        Color.Companion.getRandomExcept(pickedColors.subList(pickedColors.size - 1, pickedColors.size)),
                        dotPath!!, start, dotSpeed))
            }
        }
    }

    fun bonus() {
        playStage.addActor(BonusPool.instance.get(Bonus.Type.BOMB, Path.Type.RADIAL,
                Position.Radial(Math.random().toFloat() * 360f, SPAWN_DIST), bonusSpeed))
    }

    companion object {

        private val SPAWN_DIST = (Math.sqrt(Math.pow(Position.heightScreen.toDouble(), 2.0) + Math.pow(Position.widthScreen.toDouble(), 2.0)) / 2).toFloat()
    }

}
