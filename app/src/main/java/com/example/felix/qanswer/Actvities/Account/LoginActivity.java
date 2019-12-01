package com.example.felix.qanswer.Actvities.Account;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.felix.qanswer.Actvities.Dashboard.DashboardActivity;
import com.example.felix.qanswer.Database.Table_Userinfo;
import com.example.felix.qanswer.Database.MySQLiteHelper;
import com.example.felix.qanswer.Other.CallbackInterface;
import com.example.felix.qanswer.R;
import com.example.felix.qanswer.Server.RequestConstants;
import com.example.felix.qanswer.Server.ServerTask;
import com.example.felix.qanswer.Server.URLGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LoginActivity
 * Activity where the user can login or register
 *
 * @author David
 */

public class LoginActivity extends AppCompatActivity implements CallbackInterface
{
    EditText userNameEdt;
    EditText passwordEdt;
    Button loginButton;

    /**
     * Starts activity
     *
     * @param savedInstanceState
     * @author David
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        checkIfCanLogin();
    }

    /**
     * Creates a new database if necessary and checks if the user is already signed in
     *
     * @author David
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

            login(userName, password);
        }
    }

    /**
     * Returning the data from the server
     *
     * @param returnValue
     * @param request
     * @author David
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
                Log.d("LoginActivity", "Anmelden fehlgeschlagen");
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
     * Parse the username and password
     *
     * @param jsonString
     * @author David
     */
    private void parseJSON(String jsonString)
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
     * Initialize the views
     *
     * @author David
     */
    private void initViews()
    {
        userNameEdt = findViewById(R.id.editText_userName);
        passwordEdt = findViewById(R.id.editText_passwordID);
        loginButton = findViewById(R.id.button_loginID);

        userNameEdt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                checkIfCanLogin();
            }
        });
        passwordEdt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                checkIfCanLogin();
            }
        });
    }

    /**
     * Checks if the password is longer than 8 characters
     *
     * @author David
     */
    private void checkIfCanLogin()
    {
        String userName = userNameEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        if (userName.length() > 0 && password.length() >= 8)
        {
            loginButton.setEnabled(true);
        }
        else
        {
            loginButton.setEnabled(false);
        }
    }

    /**
     * Performs the login with starting the ServerTask
     *
     * @param userName
     * @param password
     * @author David
     */
    private void login(String userName, String password)
    {
        String urlString = URLGenerator.loginUser(userName, password);
        ServerTask serverTask = new ServerTask();
        serverTask.callbackInterface = this;
        serverTask.execute(urlString, RequestConstants.LOGIN_USER);
    }

    /**
     * Checks if the login button is clicked and performs the login logic
     *
     * @param view
     */
    public void loginButton_clicked(View view)
    {
        String userName = userNameEdt.getText().toString();
        String password = passwordEdt.getText().toString();

        login(userName, password);
    }

    /**
     * Checks if the register button is clicked and performs the register logic
     *
     * @param view
     * @author David
     */
    public void registerButton_clicked(View view)
    {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    /**
     * Inserts the current user into the database if the login was successful
     *
     * @param userID
     * @param userName
     * @param userPassword
     * @author David
     */
    private void insertIntoDB(String userID, String userName, String userPassword)
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
