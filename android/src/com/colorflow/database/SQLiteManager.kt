package com.colorflow.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.colorflow.persistence.DataRing
import com.colorflow.persistence.IStorage
import java.util.ArrayList

class SQLiteManager(context: Context):
        SQLiteOpenHelper(context, NAME, null, VERSION),
        IStorage {

    companion object {
        // SQLite db
        private const val NAME = "Data.db"
        private const val VERSION = 2

        private val TABLES = object {
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

        private val DELETE_TABLES = listOf(
                TABLES.RING.TAB_NAME,
                TABLES.STATUS.TAB_NAME
        ).joinToString("") { "DROP TABLE IF EXISTS $it;" }
    }

    private val readable: SQLiteDatabase = readableDatabase
    private val writable: SQLiteDatabase = writableDatabase

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DDL_CREATE_STATUS)
        sqLiteDatabase.execSQL(DDL_CREATE_RING)
        _init(sqLiteDatabase)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(DELETE_TABLES)
        onCreate(sqLiteDatabase)
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
        values.put(TABLES.RING.COL_SRC, "0.png")
        sqLiteDatabase.insert(TABLES.RING.TAB_NAME, null, values)
        values.clear()
        values.put(TABLES.RING.COL_ID, "1")
        values.put(TABLES.RING.COL_COST, 100)
        values.put(TABLES.RING.COL_PURCHASED, 0)
        values.put(TABLES.RING.COL_USED, 0)
        values.put(TABLES.RING.COL_SRC, "1.png")
        sqLiteDatabase.insert(TABLES.RING.TAB_NAME, null, values)
        values.clear()
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

    override var version: String
        get() {
            readable.query(TABLES.STATUS.TAB_NAME,
                    arrayOf(TABLES.STATUS.COL_VALUE),
                    "${TABLES.STATUS.COL_KEY} =?",
                    arrayOf(TABLES.STATUS.keys.VERSION), null, null, null).use {
                it.moveToNext()
                return it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE))
            }
        }
        set(version) {
            val values = ContentValues()
            values.put(TABLES.STATUS.COL_VALUE, version)
            writable.update(TABLES.STATUS.TAB_NAME,
                    values,
                    "${TABLES.STATUS.COL_KEY} =?",
                    arrayOf(TABLES.STATUS.keys.VERSION))
        }

    override var record: Int
        get() {
            readable.query(TABLES.STATUS.TAB_NAME,
                    arrayOf(TABLES.STATUS.COL_VALUE),
                    "${TABLES.STATUS.COL_KEY} =?",
                    arrayOf(TABLES.STATUS.keys.RECORD), null, null, null).use {
                it.moveToNext()
                return Integer.parseInt(it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE)))
            }
        }
        set(value) {
            val values = ContentValues()
            values.put(TABLES.STATUS.COL_VALUE, value.toString())
            writable.update(TABLES.STATUS.TAB_NAME,
                    values,
                    "${TABLES.STATUS.COL_KEY} =?",
                    arrayOf(TABLES.STATUS.keys.RECORD))
        }

    override var coins: Int
        get() {
            readable.query(TABLES.STATUS.TAB_NAME,
                    arrayOf(TABLES.STATUS.COL_VALUE),
                    "${TABLES.STATUS.COL_KEY} =?",
                    arrayOf(TABLES.STATUS.keys.COINS), null, null, null).use {
                it.moveToNext()
                return Integer.parseInt(it.getString(it.getColumnIndexOrThrow(TABLES.STATUS.COL_VALUE)))
            }
        }
        set(value) {
            val values = ContentValues()
            values.put(TABLES.STATUS.COL_VALUE, value.toString())
            writable.update(TABLES.STATUS.TAB_NAME,
                    values,
                    "${TABLES.STATUS.COL_KEY} =?",
                    arrayOf(TABLES.STATUS.keys.COINS))
        }

    override val rings: List<DataRing>
        get() {
            val ring_list = ArrayList<DataRing>()
            readable.query(TABLES.RING.TAB_NAME,
                    null, null, null, null, null, null).use {
                while (it.moveToNext()) {
                    ring_list.add(DataRing(
                            it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_ID)),
                            it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_COST)),
                            it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_PURCHASED)) == 1,
                            it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_USED)) == 1,
                            it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_SRC))))
                }
                return ring_list
            }
        }

    override var used_ring: DataRing
        get() {
            readable.query(TABLES.RING.TAB_NAME,
                    null,
                    "${TABLES.RING.COL_USED} =?",
                    arrayOf("1"), null, null, null).use {
                it.moveToNext()
                return DataRing(
                        it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_ID)),
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_COST)),
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_PURCHASED)) == 1,
                        it.getInt(it.getColumnIndexOrThrow(TABLES.RING.COL_USED)) == 1,
                        it.getString(it.getColumnIndexOrThrow(TABLES.RING.COL_SRC)))
            }
        }
        set(value) {
            val values = ContentValues()
            transaction {
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
                        arrayOf(value.id))
            }
        }

    override fun purchase_ring(id: String) {
        val values = ContentValues()
        values.put(TABLES.RING.COL_PURCHASED, 1)
        writable.update(TABLES.RING.TAB_NAME,
                values,
                "${TABLES.RING.COL_ID} =?",
                arrayOf(id))
    }

}
