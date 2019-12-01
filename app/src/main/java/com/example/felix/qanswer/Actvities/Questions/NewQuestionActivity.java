package com.example.felix.qanswer.Actvities.Questions;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.felix.qanswer.Actvities.Dashboard.DashboardActivity;
import com.example.felix.qanswer.Actvities.Profile.ProfileActivity;
import com.example.felix.qanswer.Actvities.Profile.ProfileFavoritesActivity;
import com.example.felix.qanswer.Database.Table_Userinfo;
import com.example.felix.qanswer.Models.Categories;
import com.example.felix.qanswer.Database.MySQLiteHelper;
import com.example.felix.qanswer.Other.CallbackInterface;
import com.example.felix.qanswer.R;
import com.example.felix.qanswer.Server.RequestConstants;
import com.example.felix.qanswer.Server.ServerTask;
import com.example.felix.qanswer.Server.URLGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * NewQuestion
 * Add a new question for a specific category
 *
 * @author Sebastian
 */

public class NewQuestionActivity extends AppCompatActivity implements CallbackInterface
{
    Spinner spinner_category;
    EditText editText_question_title;
    EditText editText_question_description;

    ArrayAdapter<String> adapter_spinner;
    List<String> list_categories = new ArrayList<>();

    int userId = -1;
    String username = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NewQuestionActivity()
    {
        for (Categories c : Categories.values())
        {
            if (!c.showCategory().equals("Kein Filter"))
            {
                list_categories.add(c.showCategory());
            }
        }
    }

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
        setContentView(R.layout.activity_new_question);

        getSupportActionBar().setElevation(0);

        spinner_category = findViewById(R.id.spinner_newQuestion_category);
        editText_question_title = findViewById(R.id.editText_newQuestion_question_title);
        editText_question_description = findViewById(R.id.editText_newQuestion_question_description);

        adapter_spinner = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_categories);
        spinner_category.setAdapter(adapter_spinner);

        Bundle params = getIntent().getExtras();
        String category_string = params.getString("currentCategory");

        int index_currentCategory = list_categories.indexOf(category_string);
        spinner_category.setSelection(index_currentCategory);

        getUserIDFromInternalDatabase();
    }

    /**
     * Returning the data from the server
     *
     * @param returnValue
     * @param request
     * @author Sebastian
     */
    @Override
    public void processFinish(String returnValue, String request)
    {
        switch (request)
        {
            case RequestConstants.NEW_QUESTION:
                Log.d("NewQuestionActivity", "New Qeustion added");
                break;

            default:
                Log.d("NewQuestionActivity", "Request not found");
                break;
        }
    }

    /**
     * Performs the newQuestion logic with starting the ServerTask
     *
     * @param view
     * @author Sebastian
     */
    public void saveButtonClicked(View view)
    {
        String category = spinner_category.getSelectedItem().toString();

        String url = URLGenerator.newQuestion(userId, username, editText_question_title.getText().toString(), editText_question_description.getText().toString(), category);
        ServerTask serverTask = new ServerTask();
        serverTask.callbackInterface = this;
        serverTask.execute(url, RequestConstants.NEW_QUESTION);
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Getting the current userID for adding a new question
     *
     * @author Sebastian
     */
    private void getUserIDFromInternalDatabase()
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(Table_Userinfo.USERINFO_TABLE_NAME,
                new String[]{Table_Userinfo.USERINFO_USERID, Table_Userinfo.USERINFO_USERNAME},
                Table_Userinfo.ID + " = 1",
                null,
                null,
                null,
                Table_Userinfo.ID);

        while (cursor.moveToNext())
        {
            userId = cursor.getInt(0);
            username = cursor.getString(1);
        }
        cursor.close();
    }

    /**
     * Redirecting to the
     *
     * @param view
     * @author Sebastian
     */
    public void addNewQuestionButtonClicked(View view)
    {
        spinner_category.setSelection(0);
        editText_question_description.setText("");
        editText_question_title.setText("");
    }

    /**
     * Redirecting to the ProfileActivity
     *
     * @param view
     * @author Sebastian
     */
    public void showYourAccountButtonClicked(View view)
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Redirecting to the ProfileFavoritesActivity
     *
     * @param view
     * @author Sebastian
     */
    public void showFavoriteQuestionsButtonClicked(View view)
    {
        Intent intent = new Intent(this, ProfileFavoritesActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Redirecting to the DashboardActivity
     *
     * @param view
     * @author Sebastian
     */
    public void goToHomeButtonClicked(View view)
    {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Redirecting to the DashboardActivity
     *
     * @param view
     * @author Sebastian
     */
    public void cancelButtonClicked(View view)
    {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}