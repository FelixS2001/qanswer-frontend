package com.example.felix.qanswer.Actvities.Dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.felix.qanswer.Actvities.Profile.ProfileFavoritesActivity;
import com.example.felix.qanswer.Actvities.Profile.ProfileQuestionsActivity;
import com.example.felix.qanswer.Models.Question;
import com.example.felix.qanswer.Other.CallbackInterface;
import com.example.felix.qanswer.R;
import com.example.felix.qanswer.Server.RequestConstants;
import com.example.felix.qanswer.Server.ServerTask;
import com.example.felix.qanswer.Server.URLGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * QuestionAdapter
 * Adapter for ListView in DashboardActivity
 *
 * @author Sebastian
 */

public class QuestionAdapter extends ArrayAdapter implements CallbackInterface
{
    private List<Question> list_questions;
    private List<Question> arrayList = new ArrayList<>();
    private Context context;
    private AppCompatActivity activity;

    private final String STATE_ANSWERED = "answered";

    public QuestionAdapter(@NonNull Context context, int resource, List<Question> objects, String activityIdentifier)
    {
        super(context, resource, objects);
        list_questions = objects;
        this.context = context;
        switch (activityIdentifier.toLowerCase())
        {
            case "dashboardactivity":
                activity = (DashboardActivity) context;
                break;
            case "profilefavoritesactivity":
                activity = (ProfileFavoritesActivity) context;
                break;
            case "profilequestionsactivity":
                activity = (ProfileQuestionsActivity) context;
                break;
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
            case RequestConstants.UPDATE_UPVOTE:
                if (activity instanceof DashboardActivity)
                {
                    ((DashboardActivity) activity).setLoadingIcon();
                    ((DashboardActivity) activity).loadData();
                }
                else if (activity instanceof ProfileFavoritesActivity)
                {
                    ((ProfileFavoritesActivity) activity).setLoadingIcon();
                    ((ProfileFavoritesActivity) activity).loadData();
                }
                else if (activity instanceof ProfileQuestionsActivity)
                {
                    ((ProfileQuestionsActivity) activity).setLoadingIcon();
                    ((ProfileQuestionsActivity) activity).loadData();
                }
                break;

            case RequestConstants.UPDATE_DOWNVOTE:
                if (activity instanceof DashboardActivity)
                {
                    ((DashboardActivity) activity).setLoadingIcon();
                    ((DashboardActivity) activity).loadData();
                }
                else if (activity instanceof ProfileFavoritesActivity)
                {
                    ((ProfileFavoritesActivity) activity).setLoadingIcon();
                    ((ProfileFavoritesActivity) activity).loadData();
                }
                else if (activity instanceof ProfileQuestionsActivity)
                {
                    ((ProfileQuestionsActivity) activity).setLoadingIcon();
                    ((ProfileQuestionsActivity) activity).loadData();
                }
                break;

            default:
                Log.d("QuestionAdapter", "Error while up/downvoting");
                break;
        }
    }

    /**
     * Creating Custom-Adapter
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @author Sebastian
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View v;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.activity_dashboard_list_adapter, parent, false);

        TextView textView_from_username = v.findViewById(R.id.textView_listItem_question_username);
        textView_from_username.setText(list_questions.get(position).getQuestionQuestioner());

        TextView textView_date = v.findViewById(R.id.textView_listItem_question_date);
        textView_date.setText(list_questions.get(position).getQuestionEntryDate());

        TextView textView_question = v.findViewById(R.id.textView_listItem_question_question);
        textView_question.setText(list_questions.get(position).getQuestionTitle());

        TextView textView_answers_count = v.findViewById(R.id.textView_listItem_question_answers_count);
        textView_answers_count.setText(list_questions.get(position).getAnswerCount() + " Antworten");

        TextView textView_upvotes = v.findViewById(R.id.textview_upvotes);
        textView_upvotes.setText(list_questions.get(position).getQuestionUpVotes() + "");

        Question q = list_questions.get(position);
        if (q.getQuestionState() != null)
        {
            if (q.getQuestionState().equals(STATE_ANSWERED))
            {
                textView_upvotes.setBackgroundResource(R.drawable.text_view_circle_answered);
            }
        }

        ImageView arrow_up = v.findViewById(R.id.arrow_up);
        arrow_up.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = URLGenerator.updateUpvote(list_questions.get(position).getQuestionId());
                ServerTask task = new ServerTask();
                task.callbackInterface = QuestionAdapter.this;
                task.execute(url, RequestConstants.UPDATE_UPVOTE);
            }
        });

        ImageView arrow_down = v.findViewById(R.id.arrow_down);
        arrow_down.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = URLGenerator.updateDownvote(list_questions.get(position).getQuestionId());
                ServerTask task = new ServerTask();
                task.callbackInterface = QuestionAdapter.this;
                task.execute(url, RequestConstants.UPDATE_DOWNVOTE);
            }
        });
        return v;
    }

    /**
     * Filling the ArrayList
     *
     * @param list
     * @author Sebastian
     */
    public void fillArrayList(List list)
    {
        arrayList.clear();
        arrayList.addAll(list);
    }

    /**
     * Filtering the ArrayList
     *
     * @param charText
     * @author Sebastian
     */
    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        list_questions.clear();
        if (charText.length() == 0)
        {
            list_questions.addAll(arrayList);
        }
        else
        {
            for (Question q : arrayList)
            {
                if (q.getQuestionTitle().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    list_questions.add(q);
                }
            }
        }
        notifyDataSetChanged();
    }
}
