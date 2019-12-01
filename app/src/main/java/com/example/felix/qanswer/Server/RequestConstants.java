package com.example.felix.qanswer.Server;

/**
 * RequestConstants
 * Each request has a constant. If the response from the server is returned in the processFinish()
 * you can check which request returned this result
 *
 * @author Felix
 */

public class RequestConstants
{
    public static final String CONFIRM_QUESTION = "confirmQuestion";
    public static final String DELETE_ANSWER = "deleteAnswer";
    public static final String DELETE_QUESTION = "deleteQuestion";
    public static final String EDIT_ANSWER = "editAnswer";
    public static final String EDIT_QUESTION = "editQuestion";
    public static final String LOAD_DASHBOARD = "loadDashboard";
    public static final String LOAD_MY_FAVORITE_QUESTIONS = "loadMyFavoriteQuestions";
    public static final String LOAD_MY_QUESTIONS = "loadMyQuestions";
    public static final String LOAD_PROFILE = "loadProfile";
    public static final String LOGIN_USER = "loginUser";
    public static final String NEW_ANSWER = "newAnswer";
    public static final String NEW_FAVORITE_QUESTION = "newFavoriteQuestion";
    public static final String DELETE_FAVORITE_QUESTION = "deleteFavoriteQuestion";
    public static final String NEW_QUESTION = "newQuestion";
    public static final String NOTIFY_ANSWERER = "notifyAnswerer";
    public static final String NOTIFY_QUESTIONER = "notifyQuestioner";
    public static final String REGISTER_USER = "registerUser";
    public static final String UPDATE_DOWNVOTE = "updateDownvote";
    public static final String UPDATE_UPVOTE = "updateUpvote";
    public static final String VIEW_QUESTION = "viewQuestion";

}
