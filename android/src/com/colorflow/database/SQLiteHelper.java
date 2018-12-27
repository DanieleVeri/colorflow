package com.colorflow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.colorflow.data.StorageInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniele on 28/07/17.
 */

public class SQLiteHelper extends SQLiteOpenHelper implements StorageInterface {

    private static final String
            START_COINS = "0",
            START_VERSION = "0.0.0",
            START_RING = "std.xml";

    private final SQLiteDatabase r, w;

    public SQLiteHelper(Context context) {
        super(context, DbStructure.NAME, null, DbStructure.VERSION);
        this.r = getReadableDatabase();
        this.w = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbStructure.Query.CREATE_STATUS);
        sqLiteDatabase.execSQL(DbStructure.Query.CREATE_PURCHASED);
        init(sqLiteDatabase);
    }

    private void init(SQLiteDatabase sqLiteDatabase) {
        ContentValues values = new ContentValues();
        /* Status */
        values.put(DbStructure.Tables.STATUS.KEY, DbStructure.StatusKey.COINS.toString());
        values.put(DbStructure.Tables.STATUS.VALUE, START_COINS);
        sqLiteDatabase.insert(DbStructure.Tables.STATUS.name, null, values);
        values.clear();
        values.put(DbStructure.Tables.STATUS.KEY, DbStructure.StatusKey.VERSION.toString());
        values.put(DbStructure.Tables.STATUS.VALUE, START_VERSION);
        sqLiteDatabase.insert(DbStructure.Tables.STATUS.name, null, values);
        values.clear();
        values.put(DbStructure.Tables.STATUS.KEY, DbStructure.StatusKey.RECORD.toString());
        values.put(DbStructure.Tables.STATUS.VALUE, "0");
        sqLiteDatabase.insert(DbStructure.Tables.STATUS.name, null, values);
        values.clear();
        /* Purchased */
        values.put(DbStructure.Tables.PURCHASED.TYPE, DbStructure.PurchasedType.RING.toString());
        values.put(DbStructure.Tables.PURCHASED.ID, START_RING);
        values.put(DbStructure.Tables.PURCHASED.USED, 1);
        sqLiteDatabase.insert(DbStructure.Tables.PURCHASED.name, null, values);
        values.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DbStructure.Query.DELETE_TABLES);
        onCreate(sqLiteDatabase);
    }

    public int getCoins() {
        Cursor cursor = r.query(DbStructure.Tables.STATUS.name,
                new String[]{DbStructure.Tables.STATUS.KEY, DbStructure.Tables.STATUS.VALUE},
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.COINS.toString()},
                null,
                null,
                null);
        cursor.moveToNext();
        return Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.STATUS.VALUE)));
    }

    public String getVersion() {
        Cursor cursor = r.query(DbStructure.Tables.STATUS.name,
                new String[]{DbStructure.Tables.STATUS.KEY, DbStructure.Tables.STATUS.VALUE},
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.VERSION.toString()},
                null,
                null,
                null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.STATUS.VALUE));
    }

    @Override
    public int getRecord() {
        Cursor cursor = r.query(DbStructure.Tables.STATUS.name,
                new String[]{DbStructure.Tables.STATUS.KEY, DbStructure.Tables.STATUS.VALUE},
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.RECORD.toString()},
                null,
                null,
                null);
        cursor.moveToNext();
        return Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.STATUS.VALUE)));
    }

    public List<String> getRings() {
        List<String> idList = new ArrayList<>();
        Cursor cursor = r.query(DbStructure.Tables.PURCHASED.name,
                new String[]{DbStructure.Tables.PURCHASED.ID},
                DbStructure.Tables.PURCHASED.TYPE + "=?",
                new String[]{DbStructure.PurchasedType.RING.toString()},
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            idList.add(cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.PURCHASED.ID)));
        }
        return idList;
    }

    public List<String> getBonus() {
        List<String> idList = new ArrayList<>();
        Cursor cursor = r.query(DbStructure.Tables.PURCHASED.name,
                new String[]{DbStructure.Tables.PURCHASED.ID},
                DbStructure.Tables.PURCHASED.TYPE + "=?",
                new String[]{DbStructure.PurchasedType.BONUS.toString()},
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            idList.add(cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.PURCHASED.ID)));
        }
        return idList;
    }

    @Override
    public String getUsedRing() {
        Cursor cursor = r.query(DbStructure.Tables.PURCHASED.name,
                new String[]{DbStructure.Tables.PURCHASED.ID},
                DbStructure.Tables.PURCHASED.USED + "=?",
                new String[]{"1"},
                null,
                null,
                null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.PURCHASED.ID));
    }


    public void incCoins(int coins) {
        Cursor cursor = r.query(DbStructure.Tables.STATUS.name,
                new String[]{DbStructure.Tables.STATUS.VALUE},
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.COINS.toString()},
                null,
                null,
                null);
        cursor.moveToNext();
        int startCoins = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DbStructure.Tables.STATUS.VALUE)));
        ContentValues values = new ContentValues();
        values.put(DbStructure.Tables.STATUS.VALUE, String.valueOf(startCoins + coins));
        w.update(DbStructure.Tables.STATUS.name,
                values,
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.COINS.toString()});
    }

    public void setRecord(int score) {
        ContentValues values = new ContentValues();
        values.put(DbStructure.Tables.STATUS.VALUE, String.valueOf(score));
        w.update(DbStructure.Tables.STATUS.name,
                values,
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.RECORD.toString()});
    }

    public void setVersion(String version) {
        ContentValues values = new ContentValues();
        values.put(DbStructure.Tables.STATUS.VALUE, version);
        w.update(DbStructure.Tables.STATUS.name,
                values,
                DbStructure.Tables.STATUS.KEY + "=?",
                new String[]{DbStructure.StatusKey.VERSION.toString()});
    }

    @Override
    public void setUsedRing(String ringId) {
        w.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DbStructure.Tables.PURCHASED.USED, 0);
        w.update(DbStructure.Tables.PURCHASED.name,
                values,
                DbStructure.Tables.PURCHASED.TYPE + "=?",
                new String[]{DbStructure.PurchasedType.RING.toString()});
        values.clear();
        values.put(DbStructure.Tables.PURCHASED.USED, 1);
        w.update(DbStructure.Tables.PURCHASED.name,
                values,
                DbStructure.Tables.PURCHASED.TYPE + "=? AND " + DbStructure.Tables.PURCHASED.ID + "=?",
                new String[]{DbStructure.PurchasedType.RING.toString(), ringId});
        w.setTransactionSuccessful();
        w.endTransaction();
    }

    public void purchaseRing(int cost, String id) {
        purchaseShop(cost, DbStructure.PurchasedType.RING, id);
    }

    public void purchaseBonus(int cost, String id) {
        purchaseShop(cost, DbStructure.PurchasedType.BONUS, id);
    }

    private void purchaseShop(int cost, DbStructure.PurchasedType type, String id) {
        ContentValues values = new ContentValues();
        values.put(DbStructure.Tables.PURCHASED.TYPE, type.toString());
        values.put(DbStructure.Tables.PURCHASED.ID, id);
        w.beginTransaction();
        incCoins(-cost);
        w.insert(DbStructure.Tables.PURCHASED.name, null, values);
        w.setTransactionSuccessful();
        w.endTransaction();
    }

    public void close() {
        r.close();
        w.close();
    }

}
