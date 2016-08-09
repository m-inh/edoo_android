package com.fries.edoo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHandler";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_UID = "uid";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_LOP_KHOA_HOC = "lop_khoa_hoc";
    public static final String KEY_MSSV = "massv";
    public static final String KEY_TYPE = "type";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT,"
                + KEY_LOP_KHOA_HOC + " TEXT,"
                + KEY_MSSV + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_AVATAR + " TEXT"
                + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String created_at, String lop, String mssv, String type, String ava) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_LOP_KHOA_HOC, lop); // lop khoa hoc
        values.put(KEY_MSSV, mssv); // ma so sinh vien
        values.put(KEY_TYPE, type); // loai doi tuong nguoi dung
        values.put(KEY_AVATAR, ava); // ava nguoi dung

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    //Update user
//    public void updateUser(String lopKhoaHoc){
////        SQLiteDatabase sqlite = this.getWritableDatabase();
//        HashMap<String, String> user = new HashMap<String, String>();
//        String selectQuery = "SELECT  * FROM " + TABLE_USER;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // Move to first row
//        cursor.moveToFirst();
//        if (cursor.getCount() > 0) {
//            user.put("name", cursor.getString(1));
//            user.put("email", cursor.getString(2));
//            user.put("uid", cursor.getString(3));
//            user.put("created_at", cursor.getString(4));
//        }
//        cursor.close();
//
//        String name = user.get(KEY_NAME);
//        String email = user.get(KEY_EMAIL);
//        String create_at = user.get(KEY_CREATED_AT);
//        String uid = user.get(KEY_UID);
//
//        Log.i(TAG, name);
//        Log.i(TAG, email);
//        Log.i(TAG, create_at);
//        Log.i(TAG, uid);
//        Log.i(TAG, lopKhoaHoc);
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, name);
//        values.put(KEY_EMAIL, email);
//        values.put(KEY_CREATED_AT, create_at);
//        values.put(KEY_UID, uid);
////        values.put(KEY_LOP_KHOA_HOC, lopKhoaHoc);
//
////        sqlite.update(TABLE_USER,values,KEY_UID + "=" + uid,null);
//
//        //xoa user cu
//        db.delete(TABLE_USER, null, null);
//
//        //chen 1 user moi vao bang
//        db.insert(TABLE_USER, null, values);
//
//        db.close();
//    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int indexName = cursor.getColumnIndex(KEY_NAME);
        int indexEmail = cursor.getColumnIndex(KEY_EMAIL);
        int indexLop = cursor.getColumnIndex(KEY_LOP_KHOA_HOC);
        int indexCreate = cursor.getColumnIndex(KEY_CREATED_AT);
        int indexUID = cursor.getColumnIndex(KEY_UID);
        int indexMssv = cursor.getColumnIndex(KEY_MSSV);
        int indexType = cursor.getColumnIndex(KEY_TYPE);
        int indexAva = cursor.getColumnIndex(KEY_AVATAR);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(indexName));
            user.put("email", cursor.getString(indexEmail));
            user.put("uid", cursor.getString(indexUID));
            user.put("created_at", cursor.getString(indexCreate));
            user.put("lop", cursor.getString(indexLop));
            user.put("mssv", cursor.getString(indexMssv));
            user.put("type", cursor.getString(indexType));
            user.put("avatar", cursor.getString(indexAva));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
