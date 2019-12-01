package com.example.felix.qanswer.Models;

import java.io.Serializable;

/**
 * Model-Class for a Question
 *
 * @author Sebastian
 */
public class Question implements Serializable
{
    private int userId;
    private int questionId;
    private String questionQuestioner;
    private String questionTitle;
    private String questionDescription;
    private int questionUpVotes;
    private String questionState;
    private String questionEntryDate;
    private int answerCount;

    public Question(int userId, int questionId, String questionQuestioner, String questionTitle, String questionDescription, int questionUpVotes, String questionState, String questionEntryDate, int answerCount)
    {
        this.userId = userId;
        this.questionId = questionId;
        this.questionQuestioner = questionQuestioner;
        this.questionTitle = questionTitle;
        this.questionDescription = questionDescription;
        this.questionUpVotes = questionUpVotes;
        this.questionState = questionState;
        this.questionEntryDate = questionEntryDate;
        this.answerCount = answerCount;
    }

    public int getUserId()
    {
        return userId;
    }

    public int getQuestionId()
    {
        return questionId;
    }

    public String getQuestionQuestioner()
    {
        return questionQuestioner;
    }

    public String getQuestionTitle()
    {
        return questionTitle;
    }

    public String getQuestionDescription()
    {
        return questionDescription;
    }

    public int getQuestionUpVotes()
    {
        return questionUpVotes;
    }

    public String getQuestionState()
    {
        return questionState;
    }

    public String getQuestionEntryDate()
    {
        return questionEntryDate;
    }

    public int getAnswerCount()
    {
        return answerCount;
    }

    @Override
    public String toString()
    {
        return "Question{" +
                "userId=" + userId +
                ", questionId=" + questionId +
                ", questionQuestioner='" + questionQuestioner + '\'' +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionDescription='" + questionDescription + '\'' +
                ", questionUpVotes=" + questionUpVotes +
                ", questionState='" + questionState + '\'' +
                ", questionEntryDate='" + questionEntryDate + '\'' +
                '}';
    }
}
