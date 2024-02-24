package com.kentj.ipon;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ipon_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_IPON = "ipon";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PURPOSE = "purpose";
    private static final String COLUMN_GOAL_AMOUNT = "goal_amount";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_IPON_DATE_ADDED = "ipon_date_added";


    private static final String TABLE_HULOG = "hulog";
    private static final String COLUMN_IPON_ID = "ipon_id";
    private static final String COLUMN_AMOUNT= "amount";
    private static final String COLUMN_SOURCE = "source";
    private static final String COLUMN_DATE_ADDED = "date_added";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IPON_TABLE = "CREATE TABLE " + TABLE_IPON + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PURPOSE + " TEXT," +
                COLUMN_GOAL_AMOUNT + " REAL," +
                COLUMN_DEADLINE + " TEXT," +
                COLUMN_IPON_DATE_ADDED + " TEXT" +
                ")";
        db.execSQL(CREATE_IPON_TABLE);

        String CREATE_HULOG_TABLE = "CREATE TABLE " + TABLE_HULOG + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IPON_ID + " INTEGER," +
                COLUMN_AMOUNT + " REAL," +
                COLUMN_SOURCE + " TEXT," +
                COLUMN_DATE_ADDED + " TEXT," +
                "FOREIGN KEY(" + COLUMN_IPON_ID + ") REFERENCES " + TABLE_IPON + "(" + COLUMN_ID + ")" +
                ")";
        db.execSQL(CREATE_HULOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IPON);
        onCreate(db);
    }

    public long insert_ipon(Ipon ipon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PURPOSE, ipon.getPurpose());
        values.put(COLUMN_GOAL_AMOUNT, ipon.getGoal_amount());
        values.put(COLUMN_DEADLINE, ipon.getDeadline());

        String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        values.put(COLUMN_IPON_DATE_ADDED, date);

        long id = db.insert(TABLE_IPON, null, values);
        db.close();
        return id;
    }

    public void update_ipon(int id, Ipon ipon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PURPOSE, ipon.getPurpose());
        values.put(COLUMN_GOAL_AMOUNT, ipon.getGoal_amount());
        values.put(COLUMN_DEADLINE, ipon.getDeadline());

        db.update(TABLE_IPON, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void delete_ipon(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IPON, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    @SuppressLint("Range")
    public Ipon get_ipon(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IPON, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Ipon ipon = new Ipon();
            ipon.setPurpose(cursor.getString(cursor.getColumnIndex(COLUMN_PURPOSE)));
            ipon.setGoal_amount(cursor.getDouble(cursor.getColumnIndex(COLUMN_GOAL_AMOUNT)));
            ipon.setDeadline(cursor.getString(cursor.getColumnIndex(COLUMN_DEADLINE)));
            cursor.close();
            db.close();
            return ipon;
        } else {
            return null;
        }
    }

    public double get_all_ipon_amount() {
        double totalAmount = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_GOAL_AMOUNT +
                ", (SELECT IFNULL(SUM(" + COLUMN_AMOUNT + "), 0) FROM " + TABLE_HULOG +
                " WHERE " + COLUMN_IPON_ID + " = " + TABLE_IPON + "." + COLUMN_ID + ") AS totalHulog " +
                "FROM " + TABLE_IPON, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") double goalAmount = cursor.getDouble(cursor.getColumnIndex(COLUMN_GOAL_AMOUNT));
                @SuppressLint("Range") double totalHulog = cursor.getDouble(cursor.getColumnIndex("totalHulog"));

                double finalBalance = goalAmount - totalHulog;

                if (finalBalance < 0) {
                    finalBalance = 0;
                }

                totalAmount += finalBalance;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return totalAmount;
    }

    @SuppressLint("Range")
    public List<Ipon> get_all_ipon() {
        List<Ipon> allIpon = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Use ORDER BY to sort by COLUMN_ID in descending order
        Cursor cursor = db.query(TABLE_IPON, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Ipon ipon = new Ipon();
                ipon.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID))));
                ipon.setPurpose(cursor.getString(cursor.getColumnIndex(COLUMN_PURPOSE)));
                ipon.setGoal_amount(cursor.getDouble(cursor.getColumnIndex(COLUMN_GOAL_AMOUNT)));
                ipon.setDeadline(cursor.getString(cursor.getColumnIndex(COLUMN_DEADLINE)));

                double totalHulog = getTotalHulogForIpon(db, cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));

                double finalBalance = cursor.getDouble(cursor.getColumnIndex(COLUMN_GOAL_AMOUNT)) - totalHulog;

                if (finalBalance < 0) {
                    finalBalance = 0;
                }

                if (totalHulog == 0.0) {
                    ipon.setBalance(cursor.getDouble(cursor.getColumnIndex(COLUMN_GOAL_AMOUNT)));
                } else {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    finalBalance = Double.parseDouble(decimalFormat.format(finalBalance));
                    ipon.setBalance(finalBalance);
                }

                allIpon.add(ipon);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return allIpon;
    }

    @SuppressLint("Range")
    private double getTotalHulogForIpon(SQLiteDatabase db, long iponId) {
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") AS total_hulog FROM " + TABLE_HULOG +
                " WHERE " + COLUMN_IPON_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(iponId)});

        double totalHulog = 0.0;
        if (cursor.moveToFirst()) {
            totalHulog = cursor.getDouble(cursor.getColumnIndex("total_hulog"));
        }

        cursor.close();

        return totalHulog;
    }

    @SuppressLint("Range")
    public double getTotalHulog(int iponId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") AS total_hulog FROM " + TABLE_HULOG +
                " WHERE " + COLUMN_IPON_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(iponId)});

        double totalHulog = 0.0;
        if (cursor.moveToFirst()) {
            totalHulog = cursor.getDouble(cursor.getColumnIndex("total_hulog"));
        }

        cursor.close();
        db.close();

        return totalHulog;
    }

    public long insert_hulog(Hulog hulog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IPON_ID, hulog.getIpon().getId());
        values.put(COLUMN_AMOUNT, hulog.getAmount());
        values.put(COLUMN_SOURCE, hulog.getSource());
        values.put(COLUMN_DATE_ADDED, hulog.getDate_added());

        long id = db.insert(TABLE_HULOG, null, values);
        db.close();
        return id;
    }

    @SuppressLint("Range")
    public List<Hulog> get_all_hulog(int ipon_id) {
        List<Hulog> allHulog = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HULOG, null, COLUMN_IPON_ID + " = ?", new String[]{String.valueOf(ipon_id)},
                null, null, COLUMN_DATE_ADDED + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Hulog hulog = new Hulog();
                hulog.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID))));
                hulog.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                hulog.setSource(cursor.getString(cursor.getColumnIndex(COLUMN_SOURCE)));
                hulog.setDate_added(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_ADDED)));

                allHulog.add(hulog);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return allHulog;
    }

}