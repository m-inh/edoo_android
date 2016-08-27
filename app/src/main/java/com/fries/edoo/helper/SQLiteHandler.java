package com.fries.edoo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    /**
     * Table name
     * TABLE_USER: save information of User
     */
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


    /**
     * TABLE CLASS: save class, which User was joined
     */
    private static final String TABLE_CLASS = "class";
    // Columns names
    public static final String CLASS_ID = "id";
    public static final String CLASS_CLASS_ID = "class_id";
    public static final String CLASS_CODE = "code";
    public static final String CLASS_NAME = "name";
    public static final String CLASS_TYPE = "type";
    public static final String CLASS_TEACHER_NAME = "teacher_name";
    public static final String CLASS_ADDRESS = "address";
    public static final String CLASS_PERIOD = "period";
    public static final String CLASS_DAY_OF_WEEK = "day_of_week";
    public static final String CLASS_CREDIT_COUNT = "credit_count";
    public static final String CLASS_STUDENT_COUNT = "student_count";


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

        String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASS + "("
                + CLASS_ID + " INTEGER PRIMARY KEY,"
                + CLASS_CLASS_ID + " TEXT,"
                + CLASS_CODE + " TEXT,"
                + CLASS_NAME + " TEXT,"
                + CLASS_TYPE + " TEXT,"
                + CLASS_TEACHER_NAME + " TEXT,"
                + CLASS_ADDRESS + " TEXT,"
                + CLASS_PERIOD + " TEXT,"
                + CLASS_DAY_OF_WEEK + " INTEGER,"
                + CLASS_CREDIT_COUNT + " INTEGER,"
                + CLASS_STUDENT_COUNT + " INTEGER"
                + ")";
        db.execSQL(CREATE_CLASS_TABLE);


        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);

        Log.i(TAG, "Database is Upgraded");
        // Create tables again
        onCreate(db);
    }

    // --------------------------------- User ------------------------------------------------------

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


    // --------------------------------------- Class -----------------------------------------------
    public void addClass(int id, String classId, String code, String name, String type, String teacherName, String address, String period,
                         int dayOfWeek, int creditCount, int studentCount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CLASS_ID, id);
        values.put(CLASS_CLASS_ID, classId);
        values.put(CLASS_CODE, code);
        values.put(CLASS_NAME, name);
        values.put(CLASS_TYPE, type);
        values.put(CLASS_TEACHER_NAME, teacherName);
        values.put(CLASS_ADDRESS, address);
        values.put(CLASS_PERIOD, period);
        values.put(CLASS_DAY_OF_WEEK, dayOfWeek);
        values.put(CLASS_CREDIT_COUNT, creditCount);
        values.put(CLASS_STUDENT_COUNT, studentCount);

        long result = db.insert(TABLE_CLASS, null, values);

        db.close();

        Log.d(TAG, "New class inserted into sqlite: " + result);
    }


    public ArrayList<HashMap<String, String>> getAllClasses() {
        ArrayList<HashMap<String, String>> arrClasses = new ArrayList<>();


        String selectQuery = "SELECT  * FROM " + TABLE_CLASS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int idId = cursor.getColumnIndex(CLASS_ID);
        int idClassId = cursor.getColumnIndex(CLASS_CLASS_ID);
        int idCode = cursor.getColumnIndex(CLASS_CODE);
        int idName = cursor.getColumnIndex(CLASS_NAME);
        int idType = cursor.getColumnIndex(CLASS_TYPE);
        int idTeacherName = cursor.getColumnIndex(CLASS_TEACHER_NAME);
        int idAddress = cursor.getColumnIndex(CLASS_ADDRESS);
        int idPeriod = cursor.getColumnIndex(CLASS_PERIOD);
        int idDayOfWeek = cursor.getColumnIndex(CLASS_DAY_OF_WEEK);
        int idCreditCount = cursor.getColumnIndex(CLASS_CREDIT_COUNT);
        int idStudentCount = cursor.getColumnIndex(CLASS_STUDENT_COUNT);

        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) return arrClasses;

        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> classes = new HashMap<String, String>();

            classes.put(CLASS_ID, "" + cursor.getInt(idId));
            classes.put(CLASS_CLASS_ID, cursor.getString(idClassId));
            classes.put(CLASS_CODE, cursor.getString(idCode));
            classes.put(CLASS_NAME, cursor.getString(idName));
            classes.put(CLASS_TYPE, cursor.getString(idType));
            classes.put(CLASS_TEACHER_NAME, cursor.getString(idTeacherName));
            classes.put(CLASS_ADDRESS, cursor.getString(idAddress));
            classes.put(CLASS_PERIOD, cursor.getString(idPeriod));
            classes.put(CLASS_DAY_OF_WEEK, "" + cursor.getInt(idDayOfWeek));
            classes.put(CLASS_CREDIT_COUNT, "" + cursor.getInt(idCreditCount));
            classes.put(CLASS_STUDENT_COUNT, "" + cursor.getInt(idStudentCount));

            arrClasses.add(classes);
            cursor.moveToNext();
            Log.d(TAG, "Fetching class from Sqlite: " + classes.toString());
        }

        cursor.close();
        db.close();
        // return user

        return arrClasses;
    }
}
