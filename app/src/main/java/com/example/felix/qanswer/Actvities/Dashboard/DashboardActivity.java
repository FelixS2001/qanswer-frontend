package com.example.felix.qanswer.Actvities.Dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.felix.qanswer.Actvities.Profile.ProfileActivity;
import com.example.felix.qanswer.Actvities.Profile.ProfileFavoritesActivity;
import com.example.felix.qanswer.Actvities.Questions.NewQuestionActivity;
import com.example.felix.qanswer.Actvities.Questions.ViewQuestionActivity;
import com.example.felix.qanswer.Database.Table_Userinfo;
import com.example.felix.qanswer.Models.Categories;
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
 * Dashboard
 * shows the questions for the selected Category
 *
 * @author Sebastian, David
 */
public class DashboardActivity extends AppCompatActivity implements CallbackInterface, SearchView.OnQueryTextListener
{
    private ListView listView_questionsForCategory;
    private Spinner spinner_category;
    private SearchView searchView_searchQuestion;

    private List<String> list_categories = new ArrayList();
    private ArrayAdapter adapter_spinner;
    private QuestionAdapter adapter_listview;

    private List<Question> list_questions = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    GifImageView gif;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DashboardActivity()
    {
        for (Categories c : Categories.values())
        {
            list_categories.add(c.showCategory());
        }
    }

    /**
     * Starts the activity
     *
     * @param savedInstanceState
     * @author Sebastian, David
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setElevation(0);

        listView_questionsForCategory = findViewById(R.id.listView_questions);
        spinner_category = findViewById(R.id.spinner_dashboard_category);
        searchView_searchQuestion = findViewById(R.id.searchView_searchQuestion);

        registerForContextMenu(listView_questionsForCategory);

        gif = findViewById(R.id.gifImageView_dashboard_loadingIcon);

        adapter_spinner = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list_categories);
        adapter_listview = new QuestionAdapter(this, R.layout.activity_dashboard_list_adapter, list_questions, "dashboardactivity");

        spinner_category.setAdapter(adapter_spinner);
        listView_questionsForCategory.setAdapter(adapter_listview);

        //The onClick listener, when a item was clicked, @author David
        final DashboardActivity dashboardActivity = this;
        listView_questionsForCategory.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Question question = list_questions.get(position);
                Intent viewQuestionIntent = new Intent(dashboardActivity, ViewQuestionActivity.class);
                viewQuestionIntent.putExtra("questionID", question.getQuestionId());
                startActivity(viewQuestionIntent);
            }
        });

        loadData();

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                gif.setVisibility(View.VISIBLE);
                listView_questionsForCategory.setVisibility(View.GONE);
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        searchView_searchQuestion.setOnQueryTextListener(this);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_dashboard);
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
     * Starts the Server-Request
     *
     * @author Sebastian
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        loadData();
    }

    /**
     * Creates the Contextmenu
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

        Question q = (Question) list_questions.get(position);
        if (userID == q.getUserId())
        {
            inflater.inflate(R.menu.activity_dashboard_context_ownquestion, menu);
        }
        else
        {
            inflater.inflate(R.menu.activity_dashboard_context_all, menu);
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
        Question question = list_questions.get(info.position);
        switch (item.getItemId())
        {
            case R.id.item_dashboard_activity_addToFavorites:
                MySQLiteHelper dbHelper = new MySQLiteHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(Table_Userinfo.USERINFO_TABLE_NAME, new String[]{Table_Userinfo.USERINFO_USERID}, Table_Userinfo.ID + " = 1", null, null, null, null);
                int userID = -1;
                cursor.moveToFirst();
                userID = cursor.getInt(0);

                cursor.close();

                String url = URLGenerator.newFavoriteQuestion(userID, question.getQuestionId());

                ServerTask task = new ServerTask();
                task.callbackInterface = this;
                task.execute(url, RequestConstants.NEW_FAVORITE_QUESTION);
                return true;

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
                                serverTask.callbackInterface = DashboardActivity.this;
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
            case RequestConstants.LOAD_DASHBOARD:
                list_questions.clear();
                try
                {
                    JSONArray jsonArray = new JSONArray(returnValue);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject question_jsonObject = jsonArray.getJSONObject(i);
                        int questionId = question_jsonObject.getInt("questionID");
                        String questionTitle = question_jsonObject.getString("questionTitle");
                        String questionQuestioner = question_jsonObject.getString("questionQuestioner");
                        int questionUpVotes = question_jsonObject.getInt("questionUpVotes");
                        String questionEntrydate = question_jsonObject.getString("questionEntrydate");
                        int answerCount = question_jsonObject.getInt("answerCount");
                        int userID = question_jsonObject.getInt("userID");
                        String questionState = question_jsonObject.getString("questionState");
                        Question question = new Question(userID, questionId, questionQuestioner, questionTitle, null, questionUpVotes, questionState, questionEntrydate, answerCount);

                        list_questions.add(question);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                adapter_listview.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                listView_questionsForCategory.setVisibility(View.VISIBLE);
                gif.setVisibility(View.GONE);

                break;

            case RequestConstants.DELETE_QUESTION:
                gif.setVisibility(View.VISIBLE);
                listView_questionsForCategory.setVisibility(View.GONE);
                loadData();
                break;

            case RequestConstants.NEW_FAVORITE_QUESTION:
                Toast.makeText(this, "Frage wurde zu ihren Favoriten hizugefügt", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.d("DashboardActivity", "Request not found");
                break;
        }

        adapter_listview.fillArrayList(list_questions);
    }

    /**
     * Fetching the data from the server
     *
     * @author Sebastian
     */
    public void loadData()
    {
        String category = spinner_category.getSelectedItem().toString();
        String url = URLGenerator.loadDashboard(category);
        ServerTask serverTask = new ServerTask();
        serverTask.callbackInterface = this;
        serverTask.execute(url, RequestConstants.LOAD_DASHBOARD);
    }

    /**
     * Redirecting to the NewQuestionActivity
     *
     * @param view
     * @author Sebastian
     */
    public void addNewQuestionButtonClicked(View view)
    {
        String current_category = spinner_category.getSelectedItem().toString();
        Intent intent = new Intent(this, NewQuestionActivity.class);
        intent.putExtra("currentCategory", current_category);
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
     * Refreshing the activity
     *
     * @param view
     * @author Sebastian
     */
    public void goToHomeButtonClicked(View view)
    {
        spinner_category.setSelection(0);
        loadData();
    }

    /**
     * Implementing the methods from the SearchView
     *
     * @param query
     * @return
     * @author Sebastian
     */
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    /**
     * Implementing the methods from the SearchView
     *
     * @param newText
     * @return
     * @author Sebastian
     */
    @Override
    public boolean onQueryTextChange(String newText)
    {
        adapter_listview.filter(newText);
        return false;
    }

    /**
     * Setting the loading-icon
     *
     * @author Sebastian
     */
    public void setLoadingIcon()
    {
        gif.setVisibility(View.VISIBLE);
        listView_questionsForCategory.setVisibility(View.GONE);
    }
}
