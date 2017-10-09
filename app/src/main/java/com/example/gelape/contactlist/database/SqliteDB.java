package com.example.gelape.contactlist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDB extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactlist";

    static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_NAME = "Name";
    public static final String KEY_BIO = "Bio";
    public static final String KEY_BORN = "Born";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PHOTO = "Photo";
    public static final String ID = "_id";

    public SqliteDB(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(DATABASE_NAME, "Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS +"( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_NAME + " TEXT NOT NULL, " +
                KEY_BIO + " TEXT, " +
                KEY_BORN + " TEXT, " +
                KEY_EMAIL + " TEXT" +
                KEY_PHOTO + " TEXT, " +
                ");";
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d(TABLE_CONTACTS, "created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }
}
