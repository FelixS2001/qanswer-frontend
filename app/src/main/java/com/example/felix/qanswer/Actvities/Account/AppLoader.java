package com.example.felix.qanswer.Actvities.Account;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.felix.qanswer.Actvities.Dashboard.DashboardActivity;
import com.example.felix.qanswer.Database.Table_Userinfo;
import com.example.felix.qanswer.Database.MySQLiteHelper;
import com.example.felix.qanswer.Other.CallbackInterface;
import com.example.felix.qanswer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AppLoader
 * Start-Activity which loads the data for login
 *
 * @author Sebastian
 */

public class AppLoader extends AppCompatActivity implements CallbackInterface
{
    /**
     * Starts the activity
     *
     * @param savedInstanceState
     * @author Sebastian
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_loader);
    }

    /**
     * Checks if user is already signed in
     *
     * @author Sebastian
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = mySQLiteHelper.getReadableDatabase();
        String tableName = Table_Userinfo.USERINFO_TABLE_NAME;
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            String userName = cursor.getString(cursor.getColumnIndex(Table_Userinfo.USERINFO_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(Table_Userinfo.USERINFO_PASSWORD));

            if ((!userName.equals("")) && (!password.equals("")))
            {
                Intent dashboardIntent = new Intent(this, DashboardActivity.class);
                startActivity(dashboardIntent);
                finish();
            }
        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Returning the data from the Server
     *
     * @param returnValue
     * @param request
     * @author Sebastian
     */
    @Override
    public void processFinish(String returnValue, String request)
    {
        if (returnValue == null || returnValue.equals(""))
        {
            Toast.makeText(this, "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (returnValue.substring(0, 6).contains("Error"))
            {
                Toast.makeText(this, "Anmelden fehlgeschlagen", Toast.LENGTH_LONG).show();
                Log.d("AppLoader", "Anmelden fehlgeschlagen");
            }
            else
            {
                parseJSON(returnValue);
                Intent dashboardIntent = new Intent(this, DashboardActivity.class);
                startActivity(dashboardIntent);
                finish();
            }
        }
    }

    /**
     * Parse the userName and password
     *
     * @param jsonString
     * @author Sebastian
     */
    public void parseJSON(String jsonString)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String userID = jsonObject.getString("userID");
            String userName = jsonObject.getString("userName");
            String userPassword = jsonObject.getString("userPassword");

            insertIntoDB(userID, userName, userPassword);

        } catch (JSONException e)
        {
            Log.d("LoginActivity", "Error parsing json");
            e.printStackTrace();
        }
    }

    /**
     * Inserts the current user into the database
     *
     * @param userID
     * @param userName
     * @param userPassword
     * @author Sebastian
     */
    public void insertIntoDB(String userID, String userName, String userPassword)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Table_Userinfo.USERINFO_USERID, userID);
        contentValues.put(Table_Userinfo.USERINFO_USERNAME, userName);
        contentValues.put(Table_Userinfo.USERINFO_PASSWORD, userPassword);

        MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        String tableName = Table_Userinfo.USERINFO_TABLE_NAME;

        db.delete(tableName, null, null);
        db.insert(tableName, null, contentValues);
    }
}
