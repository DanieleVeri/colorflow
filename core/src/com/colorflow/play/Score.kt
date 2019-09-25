package com.colorflow.play

class Score {
    var multiplier: Float = 1f
    var points: Int = 0
    var coins: Int = 0

    init {
        reset()
    }

    fun reset() {
        points = 0
        coins = 0
        multiplier = 1f
    }

    fun incPoints(points: Int) {
        this.points += (points * multiplier).toInt()
    }

    fun incCoins(coins: Int) {
        this.coins += coins
    }
}
