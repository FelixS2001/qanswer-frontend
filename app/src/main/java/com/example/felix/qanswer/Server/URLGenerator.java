package com.example.felix.qanswer.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * URLGenerator
 * Returns the URLs which can be sent to the server in order to fetch the data
 *
 * @author Felix
 */

public class URLGenerator
{
    public static String ROOT_URL;
    public static String CREDENTIALS;

    static
    {
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream("credentials.properties"));
            ROOT_URL = properties.getProperty("ROOT_URL");
            CREDENTIALS = properties.getProperty("CREDENTIALS");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * URL for marking a question as answered
     *
     * @param questionID
     * @param answerID
     * @return
     * @author Felix
     */
    public static String confirmQuestion(int questionID, int answerID)
    {
        String requestURL = ROOT_URL + "request=confirmQuestion&questionid=" + String.valueOf(questionID) +
                "&answerid=" + String.valueOf(answerID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for deleting the answer
     *
     * @param answerID
     * @return
     * @author Felix
     */
    public static String deleteAnswer(int answerID)
    {
        String requestURL = ROOT_URL + "request=deleteAnswer&answerid=" + String.valueOf(answerID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for deleting a favorite question
     *
     * @param userID
     * @param questionID
     * @return
     * @author Felix
     */
    public static String deleteFavoriteQuestion(int userID, int questionID)
    {
        String requestURL = ROOT_URL + "request=deleteFavoriteQuestion&userid=" + String.valueOf(userID) +
                "&questionid=" + String.valueOf(questionID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for deleting the answer
     *
     * @param questionID
     * @return
     * @author Felix
     */
    public static String deleteQuestion(int questionID)
    {
        String requestURL = ROOT_URL + "request=deleteQuestion&questionid=" + String.valueOf(questionID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for editing the answer
     *
     * @param answerID
     * @param answerTextForUpdate
     * @return
     * @author Felix
     */
    public static String editAnswer(int answerID, String answerTextForUpdate)
    {
        String requestURL = ROOT_URL + "request=editAnswer&answerid=" + String.valueOf(answerID) +
                "&answer=" + answerTextForUpdate +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for editing the question
     *
     * @param questionID
     * @param questionTitleForUpdate
     * @param questionTextForUpdate
     * @return
     * @author Felix
     */
    public static String editQuestion(int questionID, String questionTitleForUpdate, String questionTextForUpdate)
    {
        String requestURL = ROOT_URL + "request=editQuestion&questionid=" + String.valueOf(questionID) +
                "&questionTitle=" + questionTitleForUpdate +
                "&questionDescription=" + questionTextForUpdate +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for loading all Questions for the Dashboard
     *
     * @param category
     * @return
     * @author Felix
     */
    public static String loadDashboard(String category)
    {
        String requestURL = ROOT_URL + "request=loadDashboard&category=" + category +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for loading questions I like
     *
     * @param userID
     * @return
     * @author Felix
     */
    public static String loadMyFavoriteQuestions(int userID)
    {
        String requestURL = ROOT_URL + "request=loadMyFavoriteQuestions&userid=" + String.valueOf(userID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for loading all questions I asked
     *
     * @param userID
     * @return
     * @author Felix
     */
    public static String loadMyQuestions(int userID)
    {
        String requestURL = ROOT_URL + "request=loadMyQuestions&userid=" + String.valueOf(userID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for loading the profile of a user
     *
     * @param userID
     * @return
     * @author Felix
     */
    public static String loadProfile(int userID)
    {
        String requestURL = ROOT_URL + "request=loadProfile&userid=" + String.valueOf(userID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for logging the use in
     *
     * @param username
     * @param password
     * @return
     * @author Felix
     */
    public static String loginUser(String username, String password)
    {
        String requestURL = ROOT_URL + "request=loginUser&username=" + username +
                "&password=" + password +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for creating a new answer for a question
     *
     * @param userID
     * @param questionID
     * @param answerer
     * @param answer
     * @return
     * @author Felix
     */
    public static String newAnswer(int userID, int questionID, String answerer, String answer)
    {
        String requestURL = ROOT_URL + "request=newAnswer&userid=" + String.valueOf(userID) +
                "&questionid=" + String.valueOf(questionID) +
                "&answerer=" + answerer +
                "&answer=" + answer +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for adding a new favorite question
     *
     * @param userID
     * @param questionID
     * @return
     * @author Felix
     */
    public static String newFavoriteQuestion(int userID, int questionID)
    {
        String requestURL = ROOT_URL + "request=newFavoriteQuestion&userid=" + String.valueOf(userID) +
                "&questionid=" + String.valueOf(questionID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for creating a new question
     *
     * @param userID
     * @param questioner
     * @param questionTitle
     * @param questionDescription
     * @param questionCategory
     * @return
     * @author Felix
     */
    public static String newQuestion(int userID, String questioner, String questionTitle, String questionDescription, String questionCategory)
    {
        String requestURL = ROOT_URL + "request=newQuestion&userid=" + String.valueOf(userID) +
                "&questioner=" + questioner +
                "&questionTitle=" + questionTitle +
                "&questionDescription=" + questionDescription +
                "&questionCategory=" + questionCategory +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for notifying the answerer
     *
     * @param userID
     * @param questionID
     * @return
     * @author Felix
     */
    public static String notifyAnswerer(int userID, int questionID)
    {
        String requestURL = ROOT_URL + "request=notifyAnswerer&userid=" + String.valueOf(userID) +
                "&questionid=" + questionID +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for notifying the questioner
     *
     * @param userID
     * @param questionID
     * @return
     * @author Felix
     */
    public static String notifyQuestioner(int userID, int questionID)
    {
        String requestURL = ROOT_URL + "request=notifyQuestioner&userid=" + String.valueOf(userID) +
                "&questionid=" + questionID +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for adding a user
     *
     * @param username
     * @param password
     * @param email
     * @return
     * @author Felix
     */
    public static String registerUser(String username, String password, String email)
    {
        String requestURL = ROOT_URL + "request=registerUser&username=" + username +
                "&password=" + password +
                "&email=" + email +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for DownVoting a question
     *
     * @param questionID
     * @return
     * @author Felix
     */
    public static String updateDownvote(int questionID)
    {
        String requestURL = ROOT_URL + "request=updateDownvote&questionid=" + String.valueOf(questionID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for UpVoting a question
     *
     * @param questionID
     * @return
     * @author Felix
     */
    public static String updateUpvote(int questionID)
    {
        String requestURL = ROOT_URL + "request=updateUpvote&questionid=" + String.valueOf(questionID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }

    /**
     * URL for viewing the question and the answers of the question
     *
     * @param questionID
     * @return
     * @author Felix
     */
    public static String viewQuestion(int questionID)
    {
        String requestURL = ROOT_URL + "request=viewQuestion&questionid=" + String.valueOf(questionID) +
                "&auth=" + CREDENTIALS;
        return requestURL;
    }
}
