package com.colorflow.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.colorflow.state.Ring
import com.colorflow.state.Track
import com.colorflow.state.IStorage
import java.util.ArrayList

class SQLiteManager(context: Context):
        SQLiteOpenHelper(context, NAME, null, VERSION),
        IStorage {

    private val readable: SQLiteDatabase = readableDatabase
    private val writable: SQLiteDatabase = writableDatabase

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DDL_CREATE_STATUS)
        sqLiteDatabase.execSQL(DDL_CREATE_RING)
        sqLiteDatabase.execSQL(DDL_CREATE_TRACK)
        init(sqLiteDatabase)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // TODO
    }

    override fun close() {
        readable.close()
        writable.close()
        super.close()
    }

    override fun transaction(task: ()->Unit) {
        writable.beginTransaction()
        try {
            task()
            writable.setTransactionSuccessful()
        } finally {
            writable.endTransaction()
        }
    }

    override fun get_version() = get_value(TABLES.STATUS.keys.VERSION)
    override fun set_version(value: String) = set_value(TABLES.STATUS.keys.VERSION, value)

    override fun get_record() = Integer.parseInt(get_value(TABLES.STATUS.keys.RECORD))
    override fun set_record(value: Int) = set_value(TABLES.STATUS.keys.RECORD, value.toString())

    override fun get_coins() = Integer.parseInt(get_value(TABLES.STATUS.keys.COINS))
    override fun set_coins(value: Int) = set_value(TABLES.STATUS.keys.COINS, value.toString())

    override fun get_bomb_chance() = get_value(TABLES.STATUS.keys.BOMB_CHANCE).toFloat()
    override fun set_bomb_chance(value: Float) = set_value(TABLES.STATUS.keys.BOMB_CHANCE, value.toString())

    override fun get_gold_chance() = get_value(TABLES.STATUS.keys.GOLD_CHANCE).toFloat()
    override fun set_gold_chance(value: Float) = set_value(TABLES.STATUS.keys.GOLD_CHANCE, value.toString())

    protected fun get_value(key: String): String {
        readable.query(TABLES.STATUS.TAB_NAME,
                arrayOf(TABLES.STATUS.COL_VALUE),
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(key), null, null, null).use {
            it.moveToNext()
            return it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE))
        }
    }

    protected fun set_value(key: String, value: String) {
        val values = ContentValues()
        values.put(TABLES.STATUS.COL_VALUE, value)
        writable.update(TABLES.STATUS.TAB_NAME,
                values,
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(key))
    }

    override fun get_rings(): List<Ring> {
        val ring_list = ArrayList<Ring>()
        readable.query(TABLES.RING.TAB_NAME,
                null, null, null, null, null, null).use {
            while (it.moveToNext()) {
                ring_list.add(Ring(
                        it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_ID)),
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_COST)),
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_PURCHASED)) == 1,
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_USED)) == 1,
                        it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_SRC))))
            }
            return ring_list
        }
    }

    override fun set_ring_selected(id: String) {
        val values = ContentValues()
        values.put(TABLES.RING.COL_USED, 0)
        writable.update(TABLES.RING.TAB_NAME,
                values,
                "${TABLES.RING.COL_USED} =?",
                arrayOf("1"))
        values.clear()
        values.put(TABLES.RING.COL_USED, 1)
        writable.update(TABLES.RING.TAB_NAME,
                values,
                "${TABLES.RING.COL_ID} =?",
                arrayOf(id))
    }

    override fun set_ring_purchased(id: String) {
        val values = ContentValues()
        values.put(TABLES.RING.COL_PURCHASED, 1)
        writable.update(TABLES.RING.TAB_NAME,
                values,
                "${TABLES.RING.COL_ID} =?",
                arrayOf(id))
    }

    override fun get_tracks(): List<Track> {
        val track_list = ArrayList<Track>()
        readable.query(TABLES.TRACK.TAB_NAME,
                null, null, null, null, null, null).use {
            while (it.moveToNext()) {
                track_list.add(Track(
                        it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_ID)),
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_COST)),
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_PURCHASED)) == 1,
                        it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_SRC))))
            }
            return track_list
        }
    }

    override fun set_track_purchased(id: String) {
        val values = ContentValues()
        values.put(TABLES.TRACK.COL_PURCHASED, 1)
        writable.update(TABLES.TRACK.TAB_NAME,
                values,
                "${TABLES.TRACK.COL_ID} =?",
                arrayOf(id))
    }

    companion object {
        const val NAME = "Data.db"
        const val VERSION = 1

        object TABLES {
            val TRACK: TRACK = TRACK()
            val RING: RING = RING()
            val STATUS: STATUS = STATUS()}

        private val DDL_CREATE_STATUS = "CREATE TABLE ${TABLES.STATUS.TAB_NAME}(" +
                "${TABLES.STATUS.COL_KEY} TEXT," +
                "${TABLES.STATUS.COL_VALUE} TEXT," +
                "PRIMARY KEY(${TABLES.STATUS.COL_KEY}));"
        private val DDL_CREATE_RING = "CREATE TABLE ${TABLES.RING.TAB_NAME}(" +
                "${TABLES.RING.COL_ID} TEXT," +
                "${TABLES.RING.COL_COST} INTEGER," +
                "${TABLES.RING.COL_PURCHASED} INTEGER," +
                "${TABLES.RING.COL_USED} INTEGER," +
                "${TABLES.RING.COL_SRC} TEXT," +
                "PRIMARY KEY(${TABLES.RING.COL_ID}));"
        private val DDL_CREATE_TRACK = "CREATE TABLE ${TABLES.TRACK.TAB_NAME}(" +
                "${TABLES.TRACK.COL_ID} TEXT," +
                "${TABLES.TRACK.COL_COST} INTEGER," +
                "${TABLES.TRACK.COL_PURCHASED} INTEGER," +
                "${TABLES.TRACK.COL_SRC} TEXT," +
                "PRIMARY KEY(${TABLES.RING.COL_ID}));"
        private val DELETE_TABLES = listOf(
                TABLES.RING.TAB_NAME,
                TABLES.TRACK.TAB_NAME,
                TABLES.STATUS.TAB_NAME
        ).joinToString("") { "DROP TABLE IF EXISTS $it;" }
    }

}

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
        val RECORD: String = "RECORD",
        val BOMB_CHANCE: String = "BOMB_CHANCE",
        val GOLD_CHANCE: String = "GOLD_CHANCE")