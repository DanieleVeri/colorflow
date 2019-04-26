package com.colorflow.persistence

interface IStorage {
    fun transaction(task: ()->Unit)

    var version: String
    var coins: Int
    var record: Int

    var used_ring: DataRing
    fun purchase_ring(id: String)
    val rings: List<DataRing>
}
