package com.example.felix.qanswer.Actvities.Questions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.felix.qanswer.Models.Answer;
import com.example.felix.qanswer.Models.Question;
import com.example.felix.qanswer.R;

import java.util.ArrayList;

/**
 * ViewQuestionAdapter
 * Adapter for the viewQuestion
 * @author David, Sebastian
 */
public class ViewQuestionAdapter extends ArrayAdapter<Object>
{
    private ArrayList<Object> questionAndAnswerList;
    private Context context;
    private final String STATE_CONFIRMED = "confirmed";

    public ViewQuestionAdapter(@NonNull Context context, int resource, ArrayList<Object> questionAndAnswerList)
    {
        super(context, resource, questionAndAnswerList);
        this.context = context;
        this.questionAndAnswerList = questionAndAnswerList;
    }

    /**
     * Creating Custom-Adapter
     * @author David
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position == 0)
        {
            Question question = (Question) questionAndAnswerList.get(0);
            view = inflater.inflate(R.layout.activity_question_detail_question_item, parent, false);
            TextView questionerTextView = (TextView) view.findViewById(R.id.textView_questionDetail_questionUserID);
            TextView dateTextView = (TextView) view.findViewById(R.id.textView_questionDetail_questionDateID);
            TextView titleTextView = (TextView) view.findViewById(R.id.textView_questionDetail_questionTitleID);
            TextView descriptionTextView = (TextView) view.findViewById(R.id.textView_questionDetail_questionTextID);

            questionerTextView.setText(question.getQuestionQuestioner());
            dateTextView.setText(question.getQuestionEntryDate());
            titleTextView.setText(question.getQuestionTitle());
            descriptionTextView.setText(question.getQuestionDescription());
        } else
        {
            Answer answer = (Answer) questionAndAnswerList.get(position);
            view = inflater.inflate(R.layout.activity_questiondetail_answer_item, parent, false);

            if (answer.getAnswerState() != null)
            {
                if (answer.getAnswerState().equals(STATE_CONFIRMED))
                {
                    view.setBackgroundResource(R.color.yellow);
                }
            }

            TextView answererTextView = (TextView) view.findViewById(R.id.textView_questionDetail_answerUserID);
            TextView dateTextView = (TextView) view.findViewById(R.id.textView_questionDetail_answerDateID);
            TextView answerTextTextView = (TextView) view.findViewById(R.id.textView_questionDetail_answerTextID);

            answererTextView.setText(answer.getAnswerAnswerer());
            dateTextView.setText(answer.getAnswerEntryDate());
            answerTextTextView.setText(answer.getAnswerText());
        }
        return view;
    }
}
