package com.colorflow.state

interface IStorage {
    fun transaction(task: ()->Unit)

    fun get_version(): String
    fun set_version(version: String)
    fun get_coins(): Int
    fun set_coins(coins: Int)
    fun get_record(): Int
    fun set_record(record: Int)
    fun get_bomb_chance(): Float
    fun set_bomb_chance(value: Float)
    fun get_gold_chance(): Float
    fun set_gold_chance(value: Float)

    fun get_rings(): List<Ring>
    fun set_ring_selected(id: String)
    fun set_ring_purchased(id: String)

    fun get_tracks(): List<Track>
    fun set_track_purchased(id: String)
}
