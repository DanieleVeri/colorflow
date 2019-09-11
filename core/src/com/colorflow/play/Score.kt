package com.colorflow.play

class Score {
    private var multiplier: Float = 0.toFloat()
    var points: Int = 0
        private set
    var coins: Int = 0
        private set
    var record: Int = 0

    init {
        reset()
    }

    fun getMultiplier(): Float {
        return multiplier
    }

    fun setMultiplier(multiplier: Float) {
        this.multiplier = multiplier
    }

    fun incPoints(points: Int) {
        this.points += (points * multiplier).toInt()
    }

    fun incCoins(coins: Int) {
        this.coins += coins
    }

    fun reset() {
        points = 0
        coins = 0
        multiplier = 1f
    }

}
