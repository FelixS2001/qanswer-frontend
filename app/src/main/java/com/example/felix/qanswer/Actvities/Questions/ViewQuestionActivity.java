package com.example.felix.qanswer.Actvities.Questions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.felix.qanswer.Actvities.Dashboard.DashboardActivity;
import com.example.felix.qanswer.Actvities.Profile.ProfileActivity;
import com.example.felix.qanswer.Actvities.Profile.ProfileFavoritesActivity;
import com.example.felix.qanswer.Database.Table_Userinfo;
import com.example.felix.qanswer.Models.Answer;
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

import pl.droidsonroids.gif.GifImageView;

/**
 * ViewQuestionActivity
 * Shows the question and the referring answers
 *
 * @author Felix, David, Sebastian
 */

public class ViewQuestionActivity extends AppCompatActivity implements CallbackInterface
{
    private Question question;
    private ArrayList<Object> questionAndAnswerList;
    private ListView questionAndAnswerListView;
    private ViewQuestionAdapter viewQuestionAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private GifImageView gifImageView_loadingIcon;

    private final String STATE_CONFIRMED = "confirmed";

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
        setContentView(R.layout.activity_view_question);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int questionID = (int) getIntent().getExtras().getInt("questionID");
        initView();
        loadData(questionID);
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

        if (position == 0)
        {
            Question q = (Question) questionAndAnswerList.get(position);
            if (userID == q.getUserId())
            {
                inflater.inflate(R.menu.activity_viewquestion_context_menu_question, menu);
            }
            else
            {
                Toast.makeText(this, "Sie sind nicht der Verfasser dieser Frage", Toast.LENGTH_LONG).show();
            }
        }
        else if (position > 0)
        {
            Answer a = (Answer) questionAndAnswerList.get(position);
            if (userID == a.getAnswerUserID())
            {
                inflater.inflate(R.menu.activity_viewquestion_context_menu_answer, menu);
            }
            else if (question.getUserId() == userID)
            {
                if (!a.getAnswerState().equals(STATE_CONFIRMED))
                {
                    inflater.inflate(R.menu.activity_viewquestion_context_answer_questioner, menu);
                }
            }
            else
            {
                Toast.makeText(this, "Sie sind nicht der Verfasser dieser Antwort", Toast.LENGTH_LONG).show();
            }
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

        switch (item.getItemId())
        {
            case R.id.item_viewQuestion_changeAnswer:
                Answer answer = (Answer) questionAndAnswerList.get(info.position);
                final int answerID = answer.getAnswerID();
                String currentAnswerText = answer.getAnswerText();

                AlertDialog.Builder newAnswerAlert = new AlertDialog.Builder(this);
                LinearLayout newAnswerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_new_answer_dialog, null);
                TextView textView_questionTitle = newAnswerLayout.findViewById(R.id.textView_questionTitle_newAnswerDialog);
                textView_questionTitle.setText(question.getQuestionTitle());
                final EditText editText_answerText = newAnswerLayout.findViewById(R.id.editText_answerText_newAnswerDialog);
                editText_answerText.setText(currentAnswerText);
                newAnswerAlert.setView(newAnswerLayout);
                newAnswerAlert.setPositiveButton("Speichern", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String answerText = editText_answerText.getText().toString();
                        ServerTask serverTask = new ServerTask();
                        serverTask.callbackInterface = ViewQuestionActivity.this;
                        String url = URLGenerator.editAnswer(answerID, answerText);
                        serverTask.execute(url, RequestConstants.EDIT_ANSWER);
                    }
                });

                newAnswerAlert.setNegativeButton("Abbrechen", null);
                newAnswerAlert.show();
                return true;

            case R.id.item_viewQuestion_deleteAnswer:
                Answer answer2 = (Answer) questionAndAnswerList.get(info.position);
                final int answerID_delete = answer2.getAnswerID();

                AlertDialog.Builder deleteAnswerAlert = new AlertDialog.Builder(this);
                deleteAnswerAlert.setMessage("Wollen Sie diese Antwort wirklich löschen?")
                        .setCancelable(false)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                ServerTask serverTask = new ServerTask();
                                serverTask.callbackInterface = ViewQuestionActivity.this;
                                String url = URLGenerator.deleteAnswer(answerID_delete);
                                serverTask.execute(url, RequestConstants.DELETE_ANSWER);
                            }
                        });

                deleteAnswerAlert.setNegativeButton("Abbrechen", null);
                deleteAnswerAlert.show();
                return true;

            case R.id.item_viewQuestion_changeQuestion:
                final Question q = (Question) questionAndAnswerList.get(0);

                AlertDialog.Builder newQuestionAlert = new AlertDialog.Builder(this);
                LinearLayout newQuestionLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.edit_question_dialog, null);

                final EditText editText_title = newQuestionLayout.findViewById(R.id.editText_questionTitle_editQuestionDialog);
                editText_title.setText(q.getQuestionTitle());

                final EditText editText_description = newQuestionLayout.findViewById(R.id.editText_questionDescription_editQuestionDialog);
                editText_description.setText(q.getQuestionDescription());

                newQuestionAlert.setView(newQuestionLayout);
                newQuestionAlert.setPositiveButton("Speichern", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String newTitle = editText_title.getText().toString();
                        String newDescription = editText_description.getText().toString();
                        ServerTask serverTask = new ServerTask();
                        serverTask.callbackInterface = ViewQuestionActivity.this;
                        String url = URLGenerator.editQuestion(q.getQuestionId(), newTitle, newDescription);
                        serverTask.execute(url, RequestConstants.EDIT_QUESTION);
                    }
                });

                newQuestionAlert.setNegativeButton("Abbrechen", null);
                newQuestionAlert.show();

                return true;

            case R.id.item_viewQuestion_deleteQuestion:
                final Question q2 = (Question) questionAndAnswerList.get(0);
                final int questionID_delete = q2.getQuestionId();

                AlertDialog.Builder deleteQuestionAlert = new AlertDialog.Builder(this);
                deleteQuestionAlert.setMessage("Wollen Sie diese Frage inkl. allen Antworten wirklich löschen?")
                        .setCancelable(false)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                ServerTask serverTask = new ServerTask();
                                serverTask.callbackInterface = ViewQuestionActivity.this;
                                String url = URLGenerator.deleteQuestion(questionID_delete);
                                serverTask.execute(url, RequestConstants.DELETE_QUESTION);
                            }
                        });

                deleteQuestionAlert.setNegativeButton("Abbrechen", null);
                deleteQuestionAlert.show();
                return true;

            case R.id.item_viewQuestion_confirmAnswer:
                final Question q_confirm = (Question) questionAndAnswerList.get(0);
                Answer answer_confirm = (Answer) questionAndAnswerList.get(info.position);

                ServerTask serverTask_notify = new ServerTask();
                serverTask_notify.callbackInterface = ViewQuestionActivity.this;
                String url_notify = URLGenerator.notifyAnswerer(answer_confirm.getAnswerUserID(), q_confirm.getQuestionId());
                serverTask_notify.execute(url_notify, RequestConstants.NOTIFY_ANSWERER);

                ServerTask serverTask_confirm = new ServerTask();
                serverTask_confirm.callbackInterface = ViewQuestionActivity.this;
                String url_confirm = URLGenerator.confirmQuestion(q_confirm.getQuestionId(), answer_confirm.getAnswerID());
                serverTask_confirm.execute(url_confirm, RequestConstants.CONFIRM_QUESTION);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Checks if the return button is clicked
     *
     * @param item
     * @return
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
     * @author Sebastian
     */
    @Override
    public void processFinish(String returnValue, String request)
    {

        switch (request)
        {
            case RequestConstants.VIEW_QUESTION:
                parseJson(returnValue);
                questionAndAnswerListView.setVisibility(View.VISIBLE);
                gifImageView_loadingIcon.setVisibility(View.GONE);
                break;

            case RequestConstants.NEW_ANSWER:
                if (!returnValue.contains("Successful: Inserted answer"))
                {
                    questionAndAnswerList.remove(questionAndAnswerList.size() - 1);
                    Toast.makeText(this, "Hinzufügen fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadData(question.getQuestionId());
                    viewQuestionAdapter.notifyDataSetChanged();
                }
                break;
            case RequestConstants.NOTIFY_QUESTIONER:
                break;

            case RequestConstants.NOTIFY_ANSWERER:
                break;

            case RequestConstants.EDIT_ANSWER:
                gifImageView_loadingIcon.setVisibility(View.VISIBLE);
                questionAndAnswerListView.setVisibility(View.GONE);
                loadData(question.getQuestionId());
                break;

            case RequestConstants.DELETE_ANSWER:
                gifImageView_loadingIcon.setVisibility(View.VISIBLE);
                questionAndAnswerListView.setVisibility(View.GONE);
                loadData(question.getQuestionId());
                break;

            case RequestConstants.EDIT_QUESTION:
                gifImageView_loadingIcon.setVisibility(View.VISIBLE);
                questionAndAnswerListView.setVisibility(View.GONE);
                loadData(question.getQuestionId());
                break;

            case RequestConstants.DELETE_QUESTION:
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                finish();
                break;

            case RequestConstants.CONFIRM_QUESTION:
                gifImageView_loadingIcon.setVisibility(View.VISIBLE);
                questionAndAnswerListView.setVisibility(View.GONE);
                loadData(question.getQuestionId());
                break;
        }
    }

    /**
     * Fetching the data from the server
     *
     * @param questionID
     */
    private void loadData(int questionID)
    {
        questionAndAnswerList.clear();
        ServerTask serverTask = new ServerTask();
        String urlString = URLGenerator.viewQuestion(questionID);
        serverTask.callbackInterface = this;
        serverTask.execute(urlString, RequestConstants.VIEW_QUESTION);
    }

    /**
     * Parsing the JSON
     *
     * @param json
     * @author Sebastian
     */
    private void parseJson(String json)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject answerJSONObject = jsonArray.getJSONObject(i);
                if (i == 0)
                {
                    //Creates the question
                    int userID = Integer.valueOf(answerJSONObject.getString("userID"));
                    int questionID = Integer.valueOf(answerJSONObject.getString("questionID"));
                    String questionQuestioner = answerJSONObject.getString("questionQuestioner");
                    String questionTitle = answerJSONObject.getString("questionTitle");
                    String questionDescription = answerJSONObject.getString("questionDescription");
                    int questionUpVotes = Integer.valueOf(answerJSONObject.getString("questionUpVotes"));
                    String questionState = answerJSONObject.getString("questionState");
                    String questionEntrydate = answerJSONObject.getString("questionEntrydate");

                    this.question = new Question(userID, questionID, questionQuestioner, questionTitle, questionDescription, questionUpVotes, questionState, questionEntrydate, 0);
                    questionAndAnswerList.add(question);
                }
                String answerID = answerJSONObject.getString("answerID");
                String answerUserID = answerJSONObject.getString("answerUserID");
                String answerAnswerer = answerJSONObject.getString("answerAnswerer");
                String answerText = answerJSONObject.getString("answerAnswer");
                String answerState = answerJSONObject.getString("answerState");
                String answerEntrydate = answerJSONObject.getString("answerEntrydate");

                if (!answerID.equals("null"))
                {
                    Answer answer = new Answer(Integer.parseInt(answerID), answerAnswerer, answerText, answerState, answerEntrydate, Integer.parseInt(answerUserID));
                    questionAndAnswerList.add(answer);
                }
            }
            viewQuestionAdapter.notifyDataSetChanged();

            questionAndAnswerListView.setVisibility(View.VISIBLE);
            gifImageView_loadingIcon.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e)
        {
            Log.d("ViewQuestionActivity", "Error by parsing the json answers");
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Opens the dialog if the newAnswer button is clicked
     *
     * @param view
     * @author Felix, David
     */
    public void addNewAnswerButtonClicked(View view)
    {
        AlertDialog.Builder newAnswerAlert = new AlertDialog.Builder(this);
        LinearLayout newAnswerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_new_answer_dialog, null);
        TextView textView_questionTitle = newAnswerLayout.findViewById(R.id.textView_questionTitle_newAnswerDialog);
        textView_questionTitle.setText(question.getQuestionTitle());
        final EditText editText_answerText = newAnswerLayout.findViewById(R.id.editText_answerText_newAnswerDialog);
        newAnswerAlert.setView(newAnswerLayout);
        final ViewQuestionActivity viewQuestionActivity = this;
        newAnswerAlert.setPositiveButton("Speichern", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String answerText = editText_answerText.getText().toString();

                //Gets the userID From the local Database
                MySQLiteHelper database = new MySQLiteHelper(viewQuestionActivity);
                Cursor cursor = database.getReadableDatabase().query(Table_Userinfo.USERINFO_TABLE_NAME,
                        new String[]{Table_Userinfo.USERINFO_USERID, Table_Userinfo.USERINFO_USERNAME},
                        null,
                        null,
                        null,
                        null,
                        null);
                cursor.moveToFirst();
                int userID = cursor.getInt(0);
                String userName = cursor.getString(1);


                String urlNewAnswer = URLGenerator.newAnswer(userID, question.getQuestionId(), userName, answerText);
                ServerTask serverTaskNewAnswer = new ServerTask();
                serverTaskNewAnswer.callbackInterface = viewQuestionActivity;
                serverTaskNewAnswer.execute(urlNewAnswer, RequestConstants.NEW_ANSWER);

                int questionID = question.getQuestionId();
                int questioner_userID = question.getUserId();

                ServerTask serverTaskNotifyQuestioner = new ServerTask();
                serverTaskNotifyQuestioner.callbackInterface = viewQuestionActivity;
                String urlNotifyQuestioner = URLGenerator.notifyQuestioner(questioner_userID, questionID);
                serverTaskNotifyQuestioner.execute(urlNotifyQuestioner, RequestConstants.NOTIFY_QUESTIONER);
            }
        });

        newAnswerAlert.setNegativeButton("Abbrechen", null);
        newAnswerAlert.show();
    }

    /**
     * Initialize the views
     *
     * @author David
     */
    private void initView()
    {
        questionAndAnswerList = new ArrayList<>();

        questionAndAnswerListView = findViewById(R.id.listView_view_question);
        viewQuestionAdapter = new ViewQuestionAdapter(this, R.layout.activity_questiondetail_answer_item, questionAndAnswerList);
        questionAndAnswerListView.setAdapter(viewQuestionAdapter);

        registerForContextMenu(questionAndAnswerListView);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout_view_question);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                loadData(question.getQuestionId());
            }
        });

        gifImageView_loadingIcon = findViewById(R.id.gifImageView_viewQuestion_loadingIcon);
    }

    /**
     * Redirecting to the NewQuestionActivity
     *
     * @param view
     * @author David
     */
    public void addNewQuestionButtonClicked(View view)
    {
        Intent intent = new Intent(this, NewQuestionActivity.class);
        intent.putExtra("currentCategory", "Kein Filter");
        startActivity(intent);
        finish();
    }

    /**
     * Redirecting to the ProfileFavoritesActivity
     *
     * @param view
     * @author David
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
     * @author David
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
     * @author David
     */
    public void goToHomeButtonClicked(View view)
    {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
