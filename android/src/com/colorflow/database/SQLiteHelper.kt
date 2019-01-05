package com.colorflow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.colorflow.data.StorageInterface
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by daniele on 28/07/17.
 */

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION), StorageInterface {

    private val r: SQLiteDatabase = readableDatabase
    private val w: SQLiteDatabase = writableDatabase

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(DDL_CREATE_STATUS)
        sqLiteDatabase.execSQL(DDL_CREATE_PURCHASED)
        init(sqLiteDatabase)
    }

    private fun init(sqLiteDatabase: SQLiteDatabase) {
        val values = ContentValues()
        /* Status */
        values.put("KEY", StatusKey.COINS.toString())
        values.put("VALUE", START_COINS)
        sqLiteDatabase.insert("STATUS", null, values)
        values.clear()
        values.put("KEY", StatusKey.VERSION.toString())
        values.put("VALUE", START_VERSION)
        sqLiteDatabase.insert("STATUS", null, values)
        values.clear()
        values.put("KEY", StatusKey.RECORD.toString())
        values.put("VALUE", "0")
        sqLiteDatabase.insert("STATUS", null, values)
        values.clear()
        /* Purchased */
        values.put("TYPE", PurchasedType.RING.toString())
        values.put("ID", START_RING)
        values.put("USED", 1)
        sqLiteDatabase.insert("PURCHASED", null, values)
        values.clear()
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(DELETE_TABLES)
        onCreate(sqLiteDatabase)
    }

    override val coins: Int
    get() {
        val cursor = r.query("STATUS",
                arrayOf("KEY", "VALUE"),
                "KEY" + "=?",
                arrayOf(StatusKey.COINS.toString()), null, null, null)
        cursor.moveToNext()
        return Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("VALUE")))
    }

    override var version: String
        get() {
            val cursor = r.query("STATUS",
                    arrayOf("KEY", "VALUE"),
                    "KEY" + "=?",
                    arrayOf(StatusKey.VERSION.toString()), null, null, null)
            cursor.moveToNext()
            return cursor.getString(cursor.getColumnIndexOrThrow("VALUE"))
        }
        set(version: String) {
            val values = ContentValues()
            values.put("VALUE", version)
            w.update("STATUS",
                    values,
                    "KEY" + "=?",
                    arrayOf(StatusKey.VERSION.toString()))
        }

    override var record: Int
        get() {
            val cursor = r.query("STATUS",
                    arrayOf("KEY", "VALUE"),
                    "KEY" + "=?",
                    arrayOf(StatusKey.RECORD.toString()), null, null, null)
            cursor.moveToNext()
            return Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("VALUE")))
        }
        set(score: Int) {
            val values = ContentValues()
            values.put("VALUE", score.toString())
            w.update("STATUS",
                    values,
                    "KEY" + "=?",
                    arrayOf(StatusKey.RECORD.toString()))
        }

    override val rings: List<String>
    get() {
        val idList = ArrayList<String>()
        val cursor = r.query("PURCHASED",
                arrayOf("ID"),
                "TYPE" + "=?",
                arrayOf(PurchasedType.RING.toString()), null, null, null)
        while (cursor.moveToNext()) {
            idList.add(cursor.getString(cursor.getColumnIndexOrThrow("ID")))
        }
        return idList
    }

    override val bonus: List<String>
    get() {
        val idList = ArrayList<String>()
        val cursor = r.query("PURCHASED",
                arrayOf("ID"),
                "TYPE" + "=?",
                arrayOf(PurchasedType.BONUS.toString()), null, null, null)
        while (cursor.moveToNext()) {
            idList.add(cursor.getString(cursor.getColumnIndexOrThrow("ID")))
        }
        return idList
    }

    override var usedRing: String
        get() {
            val cursor = r.query("PURCHASED",
                    arrayOf("ID"),
                    "USED" + "=?",
                    arrayOf("1"), null, null, null)
            cursor.moveToNext()
            return cursor.getString(cursor.getColumnIndexOrThrow("ID"))
        }
        set(ringId: String) {
            w.beginTransaction()
            val values = ContentValues()
            values.put("USED", 0)
            w.update("PURCHASED",
                    values,
                    "TYPE" + "=?",
                    arrayOf(PurchasedType.RING.toString()))
            values.clear()
            values.put("USED", 1)
            w.update("PURCHASED",
                    values,
                    "TYPE" + "=? AND " + "ID" + "=?",
                    arrayOf(PurchasedType.RING.toString(), ringId))
            w.setTransactionSuccessful()
            w.endTransaction()
        }


    override fun incCoins(coins: Int) {
        val cursor = r.query("STATUS",
                arrayOf("VALUE"),
                "KEY" + "=?",
                arrayOf(StatusKey.COINS.toString()), null, null, null)
        cursor.moveToNext()
        val startCoins = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("VALUE")))
        val values = ContentValues()
        values.put("VALUE", (startCoins + coins).toString())
        w.update("STATUS",
                values,
                "KEY" + "=?",
                arrayOf(StatusKey.COINS.toString()))
    }

    override fun purchaseRing(cost: Int, id: String) {
        purchaseShop(cost, PurchasedType.RING, id)
    }

    override fun purchaseBonus(cost: Int, id: String) {
        purchaseShop(cost, PurchasedType.BONUS, id)
    }

    private fun purchaseShop(cost: Int, type: PurchasedType, id: String) {
        val values = ContentValues()
        values.put("TYPE", type.toString())
        values.put("ID", id)
        w.beginTransaction()
        incCoins(-cost)
        w.insert("PURCHASED", null, values)
        w.setTransactionSuccessful()
        w.endTransaction()
    }

    override fun close() {
        r.close()
        w.close()
    }

    companion object {
        // Start values
        private const val START_COINS = "0"
        private const val START_VERSION = "0.0.0"
        private const val START_RING = "std.xml"

        // Sqlite db
        private const val NAME = "Data.db"
        private const val VERSION = 1

        // DDL
        private const val DDL_CREATE_STATUS = "CREATE TABLE STATUS(" +
                "KEY TEXT," +
                "VALUE TEXT," +
                "PRIMARY KEY(KEY));"
        private const val DDL_CREATE_PURCHASED = "CREATE TABLE PURCHASED(" +
                "TYPE TEXT," +
                "ID TEXT," +
                "USED INTEGER," +
                "PRIMARY KEY(TYPE, ID));"
        private const val DELETE_TABLES = "DROP TABLE IF EXISTS PURCHASED, STATUS, STAGE"
    }

    enum class PurchasedType {
        RING, BONUS
    }

    enum class StatusKey {
        COINS, RECORD, VERSION
    }

}
