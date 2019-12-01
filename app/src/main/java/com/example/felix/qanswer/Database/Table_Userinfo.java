package com.example.felix.qanswer.Database;

/**
 * Constants for the table Userinfo
 * @author Sebastian
 */
public class Table_Userinfo
{
    public final static String USERINFO_TABLE_NAME = "Userinfo";
    public final static String ID = "_id";
    public final static String USERINFO_USERID = "UserID";
    public final static String USERINFO_USERNAME = "Username";
    public final static String USERINFO_PASSWORD = "Password";
    public final static String USERINFO_CREATE_TABLE =
            "CREATE TABLE " + USERINFO_TABLE_NAME +
                    "(" +
                    ID + " INTEGER PRIMARY KEY, " +
                    USERINFO_USERID + " INTEGER NOT NULL, " +
                    USERINFO_USERNAME + " TEXT NOT NULL, " +
                    USERINFO_PASSWORD + " TEXT NOT NULL " +
                    ");";
    public final static String USERINFO_DROP_TABLE = "DROP TABLE " + USERINFO_TABLE_NAME + ";";
}
