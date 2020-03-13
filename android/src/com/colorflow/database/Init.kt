package com.colorflow.database

import android.content.ContentValues
import com.colorflow.database.SQLiteManager.Companion.TABLES
import android.database.sqlite.SQLiteDatabase

fun init(sqLiteDatabase: SQLiteDatabase) {
    val values = ContentValues()
    // Status
    values.put(TABLES.STATUS.COL_KEY, TABLES.STATUS.keys.VERSION)
    values.put(TABLES.STATUS.COL_VALUE, SQLiteManager.VERSION)
    sqLiteDatabase.insert(TABLES.STATUS.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.STATUS.COL_KEY, TABLES.STATUS.keys.COINS)
    values.put(TABLES.STATUS.COL_VALUE, "0")
    sqLiteDatabase.insert(TABLES.STATUS.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.STATUS.COL_KEY, TABLES.STATUS.keys.RECORD)
    values.put(TABLES.STATUS.COL_VALUE, "0")
    sqLiteDatabase.insert(TABLES.STATUS.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.STATUS.COL_KEY, TABLES.STATUS.keys.BOMB_CHANCE)
    values.put(TABLES.STATUS.COL_VALUE, 0.1f)
    sqLiteDatabase.insert(TABLES.STATUS.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.STATUS.COL_KEY, TABLES.STATUS.keys.GOLD_CHANCE)
    values.put(TABLES.STATUS.COL_VALUE, 0.1f)
    sqLiteDatabase.insert(TABLES.STATUS.TAB_NAME, null, values)
    values.clear()

    // Ring
    values.put(TABLES.RING.COL_ID, "0")
    values.put(TABLES.RING.COL_COST, 0)
    values.put(TABLES.RING.COL_PURCHASED, 1)
    values.put(TABLES.RING.COL_USED, 1)
    values.put(TABLES.RING.COL_SRC, "ring")
    sqLiteDatabase.insert(TABLES.RING.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.RING.COL_ID, "1")
    values.put(TABLES.RING.COL_COST, 100)
    values.put(TABLES.RING.COL_PURCHASED, 0)
    values.put(TABLES.RING.COL_USED, 0)
    values.put(TABLES.RING.COL_SRC, "ring")
    sqLiteDatabase.insert(TABLES.RING.TAB_NAME, null, values)
    values.clear()

    // Tracks
    values.put(TABLES.TRACK.COL_ID, "[NCS] Max Brhon - Cyberpunk")
    values.put(TABLES.TRACK.COL_COST, 0)
    values.put(TABLES.TRACK.COL_PURCHASED, 1)
    values.put(TABLES.TRACK.COL_SRC, "[NCS] Max Brhon - Cyberpunk")
    sqLiteDatabase.insert(TABLES.TRACK.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.TRACK.COL_ID, "[NCS] Oneeva - Platform 9")
    values.put(TABLES.TRACK.COL_COST, 100)
    values.put(TABLES.TRACK.COL_PURCHASED, 0)
    values.put(TABLES.TRACK.COL_SRC, "[NCS] Oneeva - Platform 9")
    sqLiteDatabase.insert(TABLES.TRACK.TAB_NAME, null, values)
    values.clear()
    values.put(TABLES.TRACK.COL_ID, "[NCS] Alan Walker - Fade")
    values.put(TABLES.TRACK.COL_COST, 1000)
    values.put(TABLES.TRACK.COL_PURCHASED, 0)
    values.put(TABLES.TRACK.COL_SRC, "[NCS] Alan Walker - Fade")
    sqLiteDatabase.insert(TABLES.TRACK.TAB_NAME, null, values)
    values.clear()
}

