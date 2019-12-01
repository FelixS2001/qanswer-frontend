package com.example.felix.qanswer.Models;

/**
 * Model-Class for a Answer
 * author David
 */

public class Answer
{
    int answerID;
    String answerAnswerer;
    String answerText;
    String answerState;
    String answerEntryDate;
    int answerUserID;

    public Answer(int answerID, String answerAnswerer, String answerText, String answerState, String answerEntryDate, int answerUserID)
    {
        this.answerID = answerID;
        this.answerAnswerer = answerAnswerer;
        this.answerText = answerText;
        this.answerState = answerState;
        this.answerEntryDate = answerEntryDate;
        this.answerUserID = answerUserID;
    }

    public int getAnswerID()
    {
        return answerID;
    }

    public String getAnswerAnswerer()
    {
        return answerAnswerer;
    }

    public String getAnswerText()
    {
        return answerText;
    }

    public String getAnswerState()
    {
        return answerState;
    }

    public String getAnswerEntryDate()
    {
        return answerEntryDate;
    }

    public int getAnswerUserID()
    {
        return answerUserID;
    }
}
