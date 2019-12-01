package com.example.felix.qanswer.Actvities.Profile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.felix.qanswer.Actvities.Account.LoginActivity;
import com.example.felix.qanswer.Actvities.Dashboard.DashboardActivity;
import com.example.felix.qanswer.Actvities.Questions.NewQuestionActivity;
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
 * ProfileActivity
 * Shows the profile of the user (questionCount, answerCount, Entrydate)
 *
 * @author Felix, David
 */

public class ProfileActivity extends AppCompatActivity implements CallbackInterface
{
    TextView textView_profile_username;
    TextView textView_profile_questions;
    TextView textView_profile_answers;
    TextView textView_profile_entrydate;
    LinearLayout listView_profile_questions;

    /**
     * Starts the activity
     *
     * @param savedInstanceState
     * @author Felix
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setElevation(0);

        textView_profile_username = findViewById(R.id.textView_profile_username);
        textView_profile_questions = findViewById(R.id.textView_profile_questionsValue);
        textView_profile_answers = findViewById(R.id.textView_profile_answersValue);
        textView_profile_entrydate = findViewById(R.id.textView_profile_entrydateValue);
        listView_profile_questions = findViewById(R.id.linearLayout_profile_questions);
        loadData();

        listView_profile_questions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ProfileActivity.this, ProfileQuestionsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Adding optionsmenu
     *
     * @param menu
     * @return
     * @author David
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_profile_opitons_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Checks if logout-button is clicked and signs the user out and deletes the user in the database
     *
     * @param item
     * @return
     * @author David
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.item_profile_activity_logout)
        {
            //Removes the current User from the local database
            MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(this);
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            String tableName = Table_Userinfo.USERINFO_TABLE_NAME;
            db.delete(tableName, null, null);

            //Starts a new login activity
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Returning data from the server
     *
     * @param returnValue
     * @param request
     * @author Felix
     */
    @Override
    public void processFinish(String returnValue, String request)
    {
        switch (request)
        {
            case RequestConstants.LOAD_PROFILE:
                parseJSON(returnValue);
                break;

            default:
                Log.d("ProfileActivity", "Request not found");
                break;
        }
    }

    /**
     * Fetching the data from the server
     *
     * @author Felix
     */
    private void loadData()
    {
        MySQLiteHelper database = new MySQLiteHelper(this);
        Cursor cursor = database.getReadableDatabase().query(Table_Userinfo.USERINFO_TABLE_NAME,
                new String[]{Table_Userinfo.USERINFO_USERID},
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        int userID = cursor.getInt(0);

        ServerTask serverTask = new ServerTask();
        serverTask.callbackInterface = this;
        serverTask.execute(URLGenerator.loadProfile(userID), RequestConstants.LOAD_PROFILE);
    }

    /**
     * Parsing the JSON
     *
     * @param json
     * @author Felix
     */
    private void parseJSON(String json)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            textView_profile_username.setText(jsonObject.getString("userName"));
            textView_profile_entrydate.setText(jsonObject.getString("userEntrydate"));
            textView_profile_questions.setText(jsonObject.getString("questionCount"));
            textView_profile_answers.setText(jsonObject.getString("answerCount"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Redirectin to the NewQuestionActivity
     *
     * @param view
     * @author Felix
     */
    public void addNewQuestionButtonClicked(View view)
    {
        Intent intent = new Intent(this, NewQuestionActivity.class);
        intent.putExtra("currentCategory", "Kein Filter");
        startActivity(intent);
        finish();
    }

    /**
     * Redirectin to the ProfileFavoritesActivity
     *
     * @param view
     * @author Felix
     */
    public void showFavoriteQuestionsButtonClicked(View view)
    {
        Intent intent = new Intent(this, ProfileFavoritesActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Refreshing the activity
     *
     * @param view
     * @author Felix
     */
    public void showYourAccountButtonClicked(View view)
    {
        loadData();
    }

    /**
     * Redirectin to the DashboardActivity
     *
     * @param view
     * @author Felix
     */
    public void goToHomeButtonClicked(View view)
    {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
