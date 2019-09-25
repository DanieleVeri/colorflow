package com.colorflow.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.colorflow.Ring
import com.colorflow.Track
import com.colorflow.os.IStorage
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
        _init(sqLiteDatabase)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    override fun close() {
        readable.close()
        writable.close()
        super.close()
    }

    private fun _init(sqLiteDatabase: SQLiteDatabase) {
        val values = ContentValues()
        // Status
        values.put(TABLES.STATUS.COL_KEY, TABLES.STATUS.keys.VERSION)
        values.put(TABLES.STATUS.COL_VALUE, VERSION)
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
        values.put(TABLES.TRACK.COL_ID, "0")
        values.put(TABLES.TRACK.COL_COST, 0)
        values.put(TABLES.TRACK.COL_PURCHASED, 1)
        values.put(TABLES.TRACK.COL_SRC, "0")
        sqLiteDatabase.insert(TABLES.TRACK.TAB_NAME, null, values)
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

    override fun get_version(): String {
        readable.query(TABLES.STATUS.TAB_NAME,
                arrayOf(TABLES.STATUS.COL_VALUE),
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(TABLES.STATUS.keys.VERSION), null, null, null).use {
            it.moveToNext()
            return it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE))
        }
    }
    override fun set_version(version: String) {
        val values = ContentValues()
        values.put(TABLES.STATUS.COL_VALUE, version)
        writable.update(TABLES.STATUS.TAB_NAME,
                values,
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(TABLES.STATUS.keys.VERSION))
    }

    override fun get_record(): Int {
        readable.query(TABLES.STATUS.TAB_NAME,
                arrayOf(TABLES.STATUS.COL_VALUE),
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(TABLES.STATUS.keys.RECORD), null, null, null).use {
            it.moveToNext()
            return Integer.parseInt(it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE)))
        }
    }
    override fun set_record(value: Int) {
        val values = ContentValues()
        values.put(TABLES.STATUS.COL_VALUE, value.toString())
        writable.update(TABLES.STATUS.TAB_NAME,
                values,
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(TABLES.STATUS.keys.RECORD))
    }

    override fun get_coins(): Int {
        readable.query(TABLES.STATUS.TAB_NAME,
                arrayOf(TABLES.STATUS.COL_VALUE),
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(TABLES.STATUS.keys.COINS), null, null, null).use {
            it.moveToNext()
            return Integer.parseInt(it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE)))
        }
    }

    override fun set_coins(value: Int) {
        val values = ContentValues()
        values.put(TABLES.STATUS.COL_VALUE, value.toString())
        writable.update(TABLES.STATUS.TAB_NAME,
                values,
                "${TABLES.STATUS.COL_KEY} =?",
                arrayOf(TABLES.STATUS.keys.COINS))
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
        private const val NAME = "Data.db"
        private const val VERSION = 1

        private val TABLES = object {
            val TRACK = object {
                val TAB_NAME = "TRACK"
                val COL_ID = "ID"
                val COL_COST = "COST"
                val COL_PURCHASED = "PURCHASED"
                val COL_SRC = "SRC"
            }
            val RING = object {
                val TAB_NAME = "RING"
                val COL_ID = "ID"
                val COL_COST = "COST"
                val COL_PURCHASED = "PURCHASED"
                val COL_USED = "USED"
                val COL_SRC = "SRC"
            }
            val STATUS = object {
                val TAB_NAME = "STATUS"
                val COL_KEY = "KEY"
                val COL_VALUE = "VALUE"
                val keys = object {
                    val VERSION = "VERSION"
                    val COINS = "COINS"
                    val RECORD = "RECORD"
                }
            }
        }
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
