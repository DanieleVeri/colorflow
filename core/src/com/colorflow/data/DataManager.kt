package com.colorflow.data

import java.util.ArrayList
import java.util.Observable

/**
 * Created by daniele on 11/05/17.
 */

class DataManager(private val storageInterface: StorageInterface) : Observable() {

    val coins: Int
        get() = storageInterface.coins

    var version: String
        get() = storageInterface.version
        set(version) {
            storageInterface.version = version
            setChanged()
            notifyObservers()
        }

    val unlockedRings: List<String>
        get() = storageInterface.rings

    val unlockedBonus: List<String>
        get() = storageInterface.bonus

    var usedRing: String
        get() = storageInterface.usedRing
        set(ringId) {
            storageInterface.usedRing = ringId
            setChanged()
            notifyObservers()
        }

    var record: Int
        get() = storageInterface.record
        set(score) {
            storageInterface.record = score
            setChanged()
            notifyObservers()
        }

    fun incCoins(coins: Int) {
        storageInterface.incCoins(coins)
        setChanged()
        notifyObservers()
    }


    fun purchaseRing(cost: Int, id: String) {
        storageInterface.purchaseRing(cost, id)
        setChanged()
        notifyObservers()
    }

    fun purchaseBonus(cost: Int, id: String) {
        storageInterface.purchaseBonus(cost, id)
        setChanged()
        notifyObservers()
    }

}
