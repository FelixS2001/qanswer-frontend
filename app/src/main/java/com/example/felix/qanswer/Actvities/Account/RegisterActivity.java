package com.example.felix.qanswer.Actvities.Account;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
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
 * RegisterActivity
 * Activity where the Usser can register a new account
 *
 * @author David
 */

public class RegisterActivity extends AppCompatActivity implements CallbackInterface
{
    EditText userNameEdt;
    EditText passwordEdt;
    EditText confirmPasswordEdt;
    EditText emailEdt;
    Button registerButton;

    private String userNameLogin;
    private String passwordLogin;
    private String emailLogin;

    /**
     * Starts the activity
     *
     * @param savedInstanceState
     * @author David
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        checkCanRegister();
    }

    /**
     * Checks if the return button is clicked
     *
     * @param item
     * @return
     * @author David
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        switch (request)
        {
            case RequestConstants.REGISTER_USER:
                if (returnValue.contains("Successful: Inserted user"))
                {
                    ServerTask loginServerTask = new ServerTask();
                    loginServerTask.callbackInterface = this;
                    String loginUrlString = URLGenerator.loginUser(userNameLogin, passwordLogin);
                    loginServerTask.execute(loginUrlString, RequestConstants.LOGIN_USER);
                }
                else
                {
                    Toast.makeText(this, "Registrierung fehlgeschlagen", Toast.LENGTH_LONG).show();
                }
                break;

            case RequestConstants.LOGIN_USER:
                parseJSON(returnValue);

                Intent dashboardIntent = new Intent(this, DashboardActivity.class);
                startActivity(dashboardIntent);
                finish();
                break;

            default:
                Log.d("RegisterActivity", "Request not found");
                break;
        }
    }

    /**
     * Parse username and password
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
        userNameEdt = findViewById(R.id.editText_register_userName);
        passwordEdt = findViewById(R.id.editText_register_password);
        confirmPasswordEdt = findViewById(R.id.editText_register_confirmPassword);
        emailEdt = findViewById(R.id.editText_register_email);
        registerButton = findViewById(R.id.button_register_registerButton);

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
                checkCanRegister();
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
                checkCanRegister();
            }
        });
        confirmPasswordEdt.addTextChangedListener(new TextWatcher()
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
                checkCanRegister();
            }
        });
        emailEdt.addTextChangedListener(new TextWatcher()
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
                checkCanRegister();
            }
        });
    }

    /**
     * Checks if the input fields are filled with valid values
     *
     * @author David
     */
    private void checkCanRegister()
    {
        boolean isCorrect = true;
        String userName = userNameEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        String confirmedPassword = confirmPasswordEdt.getText().toString();
        String email = emailEdt.getText().toString();

        //Checks if the email is valid
        Boolean isEmailValid = !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (userName.length() <= 0 || password.length() < 8 || confirmedPassword.length() < 8 || !isEmailValid)
        {
            isCorrect = false;
        }
        else
        {
            if (!password.equals(confirmedPassword))
            {
                isCorrect = false;
            }
        }

        if (isCorrect)
        {
            registerButton.setEnabled(true);
        }
        else
        {
            registerButton.setEnabled(false);
        }
    }

    /**
     * Performs the register when the button is clicked with starting the ServerTask
     *
     * @param view
     * @author David
     */
    public void registerButtonRegister_clicked(View view)
    {
        String userName = userNameEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        String email = emailEdt.getText().toString();

        this.userNameLogin = userName;
        this.passwordLogin = password;
        this.emailLogin = email;

        String urlString = URLGenerator.registerUser(userName, password, email);
        ServerTask serverTask = new ServerTask();
        serverTask.callbackInterface = this;
        serverTask.execute(urlString, RequestConstants.REGISTER_USER);
    }

    /**
     * Inserts the current user into the database if registering was successful
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