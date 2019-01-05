package com.colorflow.play

import java.util.Observable

/**
 * Created by daniele on 11/05/17.
 */

class Score : Observable() {

    private var multiplier: Float = 0.toFloat()
    var points: Int = 0
        private set
    var coins: Int = 0
        private set

    init {
        reset()
    }

    fun getMultiplier(): Float {
        return multiplier
    }

    fun setMultiplier(multiplier: Float) {
        this.multiplier = multiplier
        setChanged()
        notifyObservers()
    }

    fun incPoints(points: Int) {
        this.points += (points * multiplier).toInt()
        setChanged()
        notifyObservers()
    }

    fun incCoins(coins: Int) {
        this.coins += coins
        setChanged()
        notifyObservers()
    }

    fun reset() {
        points = 0
        coins = 0
        multiplier = 1f
        setChanged()
        notifyObservers()
    }

}
