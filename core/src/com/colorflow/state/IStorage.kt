package com.colorflow.state

interface IStorage {
    fun get_version(): String
    fun set_version(version: String)
    fun transaction(task: ()->Unit)

    fun get_coins(): Int
    fun set_coins(coins: Int)

    fun get_record(): Int
    fun set_record(record: Int)

    fun get_rings(): List<Ring>
    fun set_ring_selected(id: String)
    fun set_ring_purchased(id: String)

    fun get_tracks(): List<Track>
    fun set_track_purchased(id: String)
}
