package com.example.gelape.contactlist.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.example.gelape.contactlist.model.ContactResponse;
import java.util.ArrayList;
import static com.example.gelape.contactlist.database.SqliteDB.ID;
import static com.example.gelape.contactlist.database.SqliteDB.KEY_BIO;
import static com.example.gelape.contactlist.database.SqliteDB.KEY_BORN;
import static com.example.gelape.contactlist.database.SqliteDB.KEY_EMAIL;
import static com.example.gelape.contactlist.database.SqliteDB.KEY_NAME;
import static com.example.gelape.contactlist.database.SqliteDB.KEY_PHOTO;
import static com.example.gelape.contactlist.database.SqliteDB.TABLE_COLUMNS;
import static com.example.gelape.contactlist.database.SqliteDB.TABLE_CONTACTS;

public class DbController
{
    private SQLiteDatabase db;
    private SqliteDB bank;
    Context context;

    public DbController(Context context)
    {
        this.context = context;
        bank = new SqliteDB(context);
    }

    public String insertData(int Id, String Name ,String Born , String Bio , String Email, String Photo)
    {
        ContentValues values;
        long result;

        db = bank.getWritableDatabase();
        values = new ContentValues();

        values.put(SqliteDB.ID, Id);
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

    public synchronized DbController open() throws SQLException
    {
        bank = new SqliteDB(context);
        db = bank.getWritableDatabase();
        return this;
    }

    public synchronized void close()
    {
        bank.close();
    }

    public long createRow(ContactResponse contact)
    {
        ContentValues values = createContentValue(contact);
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public boolean updateRow(long rowIndex, ContactResponse contact)
    {
        ContentValues values = createContentValue(contact);
        return db.update(TABLE_CONTACTS, values, ID + "=" + rowIndex, null) > 0;
    }

    public boolean deleteRow(long rowIndex)
    {
        return db.delete(TABLE_CONTACTS, ID + "=" + rowIndex, null) > 0;
    }

    public Cursor fetchAllEntries()
    {
        return db.query(TABLE_CONTACTS, TABLE_COLUMNS, null, null, null, null, null);
    }

    public ArrayList<ContactResponse> fetchAllContacts()
    {
        ArrayList<ContactResponse> res = new ArrayList<>();

        Cursor resultSet = fetchAllEntries();

        if (resultSet.moveToFirst())
            for(int i = 0; i < resultSet.getCount(); i++)
            {
                int id = resultSet.getInt(resultSet.getColumnIndex(ID));
                String name = resultSet.getString(resultSet.getColumnIndex(KEY_NAME));
                String bio = resultSet.getString(resultSet.getColumnIndex(KEY_BIO));
                String born = resultSet.getString(resultSet.getColumnIndex(KEY_BORN));
                String email = resultSet.getString(resultSet.getColumnIndex(KEY_EMAIL));
                String photo = resultSet.getString(resultSet.getColumnIndex(KEY_PHOTO));

                ContactResponse c = new ContactResponse(id, name, bio, born, email, photo);

                res.add(c);
                if(!resultSet.moveToNext())
                    break;
            }
        resultSet.close();
        return res;
    }

    private ContentValues createContentValue(ContactResponse contact)
    {
        ContentValues values = new ContentValues();
        values.put(ID,contact.getId());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_BIO, contact.getBio());
        values.put(KEY_BORN, contact.getBorn());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_PHOTO, contact.getPhoto());
        return values;
    }
}
