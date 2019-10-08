package com.colorflow.database

object TABLES {
        val TRACK: TRACK = TRACK()
        val RING: RING = RING()
        val STATUS: STATUS = STATUS()}

data class TRACK (
        val TAB_NAME: String = "TRACK",
        val COL_ID: String = "ID",
        val COL_COST: String = "COST",
        val COL_PURCHASED: String = "PURCHASED",
        val COL_SRC: String = "SRC")

data class RING (
        val TAB_NAME: String = "RING",
        val COL_ID: String = "ID",
        val COL_COST: String = "COST",
        val COL_PURCHASED: String = "PURCHASED",
        val COL_USED: String = "USED",
        val COL_SRC: String = "SRC")

data class STATUS (
        val TAB_NAME: String = "STATUS",
        val COL_KEY: String = "KEY",
        val COL_VALUE: String = "VALUE",
        val keys: Keys = Keys())

data class Keys (
        val VERSION: String = "VERSION",
        val COINS: String = "COINS",
        val RECORD: String = "RECORD")