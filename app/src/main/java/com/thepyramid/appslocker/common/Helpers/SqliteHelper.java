package com.thepyramid.appslocker.common.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class SqliteHelper extends SQLiteOpenHelper {

    public static final int PACKAGE_NAME_INDEX = 0;
    public static final int APP_NAME_INDEX = 1;
    public static final int IMAGE_INDEX = 2;


    public static final String PACKAGE_NAME_COLUMN = "Packagename";
    public static final String APP_NAME_COLUMN = "Appname";
    public static final String IMAGE_COLUMN = "image";



    private static final String DB_NAME = "lockedApps.sqlite";
    private static final String DB_PATH = "/data/data/com.thepyramid.appslocker/databases/";
    private static final String FULL_DB_PATH = DB_PATH + DB_NAME;
    public static final String LOCKED_APPS_TABLE_NAME = "LockedApps";

    private Context context;
    private SQLiteDatabase sqLiteDatabase;


    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        createDb();
        openDb();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    private void createDb() {
        boolean check = checkDb();
        if (!check) {
            this.getReadableDatabase();
            copyDb();
        }
    }

    private boolean checkDb() {
        SQLiteDatabase database = null;
        try {
            close();
            database = SQLiteDatabase.openDatabase(FULL_DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (database != null) {
            database.close();
        }
        return database != null;
    }

    private void openDb() {
        try {
            close();
            sqLiteDatabase = SQLiteDatabase.openDatabase(FULL_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void copyDb() {
        try {
            InputStream inputStream = context.getAssets().open(DB_NAME);
            OutputStream outputStream = new FileOutputStream(FULL_DB_PATH);
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long insertRow(String table, String whereClause, ContentValues contentValues) {
        long res = -1;
        sqLiteDatabase.beginTransaction();
        try {
            res = sqLiteDatabase.insert(table, whereClause, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return res;
    }

    public Cursor selectAll(String table) {
        Cursor cursor = null;
        String sql = "select * from " + table;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, new String[]{});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    public int deleteRow(String tableName, String selection, String[] selectionArgs) {
        return sqLiteDatabase.delete(tableName, selection, selectionArgs);
    }

    public Cursor selectRow(String table, String[] projection, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {
        Cursor cursor = null;
        cursor = sqLiteDatabase.query(table, projection, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }


    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }
}
