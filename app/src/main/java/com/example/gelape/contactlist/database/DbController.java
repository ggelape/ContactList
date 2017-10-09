package com.example.gelape.contactlist.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbController
{
    private SQLiteDatabase db;
    private SqliteDB bank;

    public DbController(Context context)
    {
        bank = new SqliteDB(context);
    }

    public String insertData(String Name ,String Born , String Bio , String Email, String Photo)
    {
        ContentValues values;
        long result;

        db = bank.getWritableDatabase();
        values = new ContentValues();
        //  Insert values
        values.put(SqliteDB.KEY_NAME, Name);
        values.put(SqliteDB.KEY_BORN, Born);
        values.put(SqliteDB.KEY_BIO, Bio);
        values.put(SqliteDB.KEY_EMAIL, Email);
        values.put(SqliteDB.KEY_PHOTO, Photo);

        result = db.insert(SqliteDB.TABLE_CONTACTS, null, values);
        db.close();
        if (result == 0)
            return "Failed";
        else
            return "Success";
    }

    public Cursor loadData()
    {
        Cursor cursor;
        String[] fields =  {bank.ID,bank.KEY_NAME};
        db = bank.getReadableDatabase();
        cursor = db.query(bank.TABLE_CONTACTS, fields, null, null, null, null, bank.ID + " ASC", null);

        if(cursor!=null)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
