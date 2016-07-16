package com.ndstudio.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Nishchhal on 10-Jun-16.
 */
public class DBHandler {

    public static final String DB_UNAME = "username";
    public static final String DB_UPASS = "password";
    public static final String DB_Balance = "balance";


    public static final String DB_NAME = "Contacts";
    public static final String DB_TABLE = "Login";
    public static final String DB_CREATE = "CREATE TABLE "+DB_TABLE+" ("+DB_UNAME+" TEXT PRIMARY KEY ,"+DB_UPASS+" TEXT, "+DB_Balance+" INTEGER DEFAULT 1000)";
    public static final int DB_VERSION = 1;

    public SQLiteDatabase db;
    public SQLHelper helper;
    public Context context;

    DBHandler(Context context)
    {
        this.context=context;
        helper = new SQLHelper();
        db = helper.getWritableDatabase();
    }

    public DBHandler openReadable() throws SQLiteException
    {
        helper = new SQLHelper();
        db = helper.getReadableDatabase();
        return this;
    }

    public long addUser(String username,String password)
    {
        ContentValues cv = new ContentValues();
        cv.put(DB_UNAME,username);
        cv.put(DB_UPASS,password);
        return db.insert(DB_TABLE,null,cv);
    }

    public boolean checkEntry(String username,String password)
    {
        String column[] = {DB_UNAME,DB_UPASS};
        db = helper.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,column,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            if(username.equals(cursor.getString(0)))
            {

                if (password.equals(cursor.getString(1))) {
                    return true;
                }
            }
            cursor.moveToNext();
        }
        return false;
    }

    public String forgot(String username)
    {
        String column[] = {DB_UNAME,DB_UPASS};
        String pass;
        Cursor cursor = db.query(DB_TABLE,column,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            if (username.equals(cursor.getString(0))) {
                pass = cursor.getString(1);
                return pass;
            }
            cursor.moveToNext();
        }
        return " ";
    }

    public int bal(String username)
    {
        String column[] = {DB_Balance};
        Cursor cursor = db.query(DB_TABLE,column,"username = '"+username+"'",null,null,null,null,null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("balance"));
    }

    public void deleteAcc(String username)
    {
        db.delete(DB_TABLE,DB_UNAME+" = '"+username+"'",null);
    }

    public void accUpdate(String username, int amt)
    {
        ContentValues cv = new ContentValues();
        cv.put(DB_Balance,this.bal(username)+amt);
        db.update(DB_TABLE,cv,DB_UNAME+" = '"+username+"'",null);
    }

    public class SQLHelper extends SQLiteOpenHelper
    {

        public SQLHelper() {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
            Log.d("Upgrade","DATABASE TABLE Upgrade from Version "+oldVersion+" to "+newVersion);
            onCreate(db);
        }
    }
}
