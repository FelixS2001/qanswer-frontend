package com.example.felix.qanswer.Actvities.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.felix.qanswer.Actvities.Dashboard.DashboardActivity;
import com.example.felix.qanswer.Actvities.Dashboard.QuestionAdapter;
import com.example.felix.qanswer.Actvities.Questions.NewQuestionActivity;
import com.example.felix.qanswer.Actvities.Questions.ViewQuestionActivity;
import com.example.felix.qanswer.Database.Table_Userinfo;
import com.example.felix.qanswer.Models.Question;
import com.example.felix.qanswer.Database.MySQLiteHelper;
import com.example.felix.qanswer.Other.CallbackInterface;
import com.example.felix.qanswer.R;
import com.example.felix.qanswer.Server.RequestConstants;
import com.example.felix.qanswer.Server.ServerTask;
import com.example.felix.qanswer.Server.URLGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * ProfileQuestionsActivity
 * Shows all questions a user has asked
 *
 * @author Felix, Sebastian
 */

public class ProfileQuestionsActivity extends AppCompatActivity implements CallbackInterface
{
    private List<Question> questionList;
    private ListView questionListView;
    private QuestionAdapter questionAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    GifImageView gif;

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
        setContentView(R.layout.activity_profile_questions);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        loadData();
    }

    /**
     * Checks if you are the owner of the question and then perform the action of the contextMenu
     *
     * @param menu
     * @param v
     * @param menuInfo
     * @author Sebastian
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        MySQLiteHelper database = new MySQLiteHelper(this);
        Cursor cursor = database.getReadableDatabase().query(Table_Userinfo.USERINFO_TABLE_NAME,
                new String[]{Table_Userinfo.USERINFO_USERID, Table_Userinfo.USERINFO_USERNAME},
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        int userID = cursor.getInt(0);

        MenuInflater inflater = getMenuInflater();

        Question q = (Question) questionList.get(position);
        if (userID == q.getUserId())
        {
            inflater.inflate(R.menu.activity_dashboard_context_ownquestion, menu);
        }
    }

    /**
     * Checks if the Contextmenu is clicked
     *
     * @param item
     * @return
     * @author Sebastian
     */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Question question = questionList.get(info.position);
        switch (item.getItemId())
        {
            case R.id.item_dashboard_activity_addToFavorites_own:
                MySQLiteHelper dbHelper_own = new MySQLiteHelper(this);
                SQLiteDatabase db_own = dbHelper_own.getReadableDatabase();
                Cursor cursor_own = db_own.query(Table_Userinfo.USERINFO_TABLE_NAME, new String[]{Table_Userinfo.USERINFO_USERID}, Table_Userinfo.ID + " = 1", null, null, null, null);
                int userID_own = -1;
                cursor_own.moveToFirst();
                userID_own = cursor_own.getInt(0);

                cursor_own.close();

                String url_own = URLGenerator.newFavoriteQuestion(userID_own, question.getQuestionId());

                ServerTask task_own = new ServerTask();
                task_own.callbackInterface = this;
                task_own.execute(url_own, RequestConstants.NEW_FAVORITE_QUESTION);
                return true;

            case R.id.item_dashboard_activity_delete_own:
                final int questionID_delete = question.getQuestionId();

                AlertDialog.Builder deleteQuestionAlert = new AlertDialog.Builder(this);
                deleteQuestionAlert.setMessage("Wollen Sie diese Frage inkl. allen Antworten wirklich löschen?")
                        .setCancelable(false)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                ServerTask serverTask = new ServerTask();
                                serverTask.callbackInterface = ProfileQuestionsActivity.this;
                                String url = URLGenerator.deleteQuestion(questionID_delete);
                                serverTask.execute(url, RequestConstants.DELETE_QUESTION);
                            }
                        });

                deleteQuestionAlert.setNegativeButton("Abbrechen", null);
                deleteQuestionAlert.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    /**
     * Check if the return button is clicked
     *
     * @param item
     * @return
     * @author Felix
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
     * @author Felix
     */
    @Override
    public void processFinish(String returnValue, String request)
    {
        switch (request)
        {
            case RequestConstants.LOAD_MY_QUESTIONS:
                parseJson(returnValue);
                break;

            case RequestConstants.DELETE_QUESTION:
                gif.setVisibility(View.VISIBLE);
                questionListView.setVisibility(View.GONE);
                loadData();
                break;

            case RequestConstants.NEW_FAVORITE_QUESTION:
                Toast.makeText(this, "Frage wurde zu ihren Favoriten hizugefügt", Toast.LENGTH_SHORT).show();
                break;

            default:
                Log.d("ProfileQuestionsActivity", "Request not found");
                break;
        }
    }

    /**
     * Fetching data from the server
     *
     * @author Felix
     */
    public void loadData()
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
        String url = URLGenerator.loadMyQuestions(userID);
        ServerTask serverTask = new ServerTask();
        serverTask.callbackInterface = this;
        serverTask.execute(url, RequestConstants.LOAD_MY_QUESTIONS);
    }

    /**
     * Parsing the JSON
     *
     * @param json
     * @author Felix
     */
    private void parseJson(String json)
    {
        questionList.clear();
        try
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject question_jsonObject = jsonArray.getJSONObject(i);
                int userID = question_jsonObject.getInt("userID");
                int questionId = question_jsonObject.getInt("questionID");
                String questionTitle = question_jsonObject.getString("questionTitle");
                String questionQuestioner = question_jsonObject.getString("questionQuestioner");
                String questionDescription = question_jsonObject.getString("questionDescription");
                int questionUpVotes = question_jsonObject.getInt("questionUpVotes");
                String questionEntrydate = question_jsonObject.getString("questionEntrydate");
                String questionState = question_jsonObject.getString("questionState");
                int answerCount = question_jsonObject.getInt("answerCount");

                Question question = new Question(userID, questionId, questionQuestioner, questionTitle, questionDescription, questionUpVotes, questionState, questionEntrydate, answerCount);
                questionList.add(question);
            }

            questionAdapter.notifyDataSetChanged();
            GifImageView gif = findViewById(R.id.gifImageView_profile_questions_loadingIcon);
            questionListView.setVisibility(View.VISIBLE);
            gif.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the views
     *
     * @author Felix
     */
    private void initViews()
    {
        questionList = new ArrayList<>();
        questionListView = findViewById(R.id.listView_profile_questions);
        gif = findViewById(R.id.gifImageView_profile_questions_loadingIcon);

        final ProfileQuestionsActivity profileQuestionsActivity = this;
        questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Question question = questionList.get(position);
                Intent viewQuestionIntent = new Intent(profileQuestionsActivity, ViewQuestionActivity.class);
                viewQuestionIntent.putExtra("questionID", question.getQuestionId());
                startActivity(viewQuestionIntent);
            }
        });
        questionAdapter = new QuestionAdapter(this, R.layout.activity_dashboard_list_adapter, questionList, "profilequestionsactivity");
        questionListView.setAdapter(questionAdapter);
        registerForContextMenu(questionListView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_profile_questions);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                loadData();
            }
        });
    }

    /**
     * Redirecting to the NewQuestionActivity
     *
     * @param view
     * @author Felix
     */
    public void addNewQuestionButtonClicked(View view)
    {
        Intent intent = new Intent(this, NewQuestionActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Redirecting to the ProfileFavoritesActivity
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
     * Redirecting to the ProfileActivity
     *
     * @param view
     * @author Felix
     */
    public void showYourAccountButtonClicked(View view)
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Redirecting to the DashboardActivity
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

    /**
     * Setting the loading-icon
     *
     * @author Felix
     */
    public void setLoadingIcon()
    {
        gif.setVisibility(View.VISIBLE);
        questionListView.setVisibility(View.GONE);
    }
}