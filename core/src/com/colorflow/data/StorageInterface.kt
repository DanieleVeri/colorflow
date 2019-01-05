package com.colorflow.data

/**
 * Created by daniele on 28/07/17.
 */

interface StorageInterface {
    val coins: Int
    var version: String
    var record: Int
    val rings: List<String>
    val bonus: List<String>
    var usedRing: String
    fun incCoins(coins: Int)
    fun purchaseRing(cost: Int, id: String)
    fun purchaseBonus(cost: Int, id: String)
}
