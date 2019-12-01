package com.example.felix.qanswer.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.felix.qanswer.Database.Table_Userinfo;

/**
 * MySQLiteHelper
 * Create the local database which stores the current user
 *
 * @author Sebastian
 */
public class MySQLiteHelper extends SQLiteOpenHelper
{
    private final static String DB_NAME = "qanswer.db";
    private final static int DB_VERSION = 1;

    public MySQLiteHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates the database
     *
     * @param db
     * @author Sebastian
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Table_Userinfo.USERINFO_CREATE_TABLE);
    }

    /**
     * Upgrades the database
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     * @author Sebastian
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(Table_Userinfo.USERINFO_DROP_TABLE);
        onCreate(db);
    }
}
